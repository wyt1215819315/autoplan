package com.github.push.base.init;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.github.push.base.annotation.PushEntity;
import com.github.push.base.annotation.PushProperty;
import com.github.push.base.annotation.PushPropertyOptions;
import com.github.push.base.dto.PushConfigDto;
import com.github.push.base.dto.PushConfigOptions;
import com.github.push.base.model.PushBaseConfig;
import com.github.push.base.service.PushService;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.dto.customform.CustomFormDisplayDto;
import com.github.system.base.dto.customform.CustomFormDisplayOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class PushInit implements CommandLineRunner {

    // 推送线程池 每个推送类型一个线程池 用于控制流速使用
    public static final ConcurrentHashMap<String, ExecutorService> pushThreadMap = new ConcurrentHashMap<>();
    public static final Map<String, Class<? extends PushBaseConfig>> pushBaseConfigMap = new HashMap<>();
    public static final Map<String, Class<? extends PushService<?>>> pushServiceMap = new HashMap<>();
    // 用于前端回显 储存所有可用的推送类型
    public static final List<String> pushTypeList = new ArrayList<>();
    // 用于前端回显 储存可用的推送字段信息
    public static final Map<String, List<CustomFormDisplayDto>> pushConfigMap = new LinkedHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        // 扫描所有推送服务并缓存
//        String packageName = this.getClass().getPackageName();
//        // 获取所有推送实体类
//        Set<Class<?>> classes = ClassUtil.scanPackageBySuper(StrUtil.subBefore(packageName, ".", true) + ".model.impl", PushBaseConfig.class);
        Set<Class<?>> classes = ClassUtil.scanPackageBySuper(SystemConstant.BASE_PACKAGE + ".push", PushBaseConfig.class);
        Set<Class<?>> pushServiceClasses = ClassUtil.scanPackageBySuper(SystemConstant.BASE_PACKAGE + ".push", PushService.class);
        // 通过Map存储key和对应的order值
        Map<String, Integer> orderMap = new HashMap<>();
        for (Class<?> aClass : classes) {
            PushEntity apiModelAnno = AnnotationUtil.getAnnotation(aClass, PushEntity.class);
            if (apiModelAnno != null) {
                String key = apiModelAnno.value();
                boolean find = false;
                for (Class<?> pushServiceClass : pushServiceClasses) {
                    PushService<?> pushService = (PushService<?>) pushServiceClass.getDeclaredConstructor().newInstance();
                    String name = pushService.getName();
                    if (key.equals(name)) {
                        find = true;
                        pushServiceMap.put(key, (Class<? extends PushService<?>>) pushServiceClass);
                    }
                }
                if (!find) {
                    log.warn("未找到key:{}对应的实现服务", key);
                    continue;
                }
                pushBaseConfigMap.put(key, (Class<? extends PushBaseConfig>) aClass);
                pushThreadMap.put(key, createThread(key));
                pushTypeList.add(key);
                orderMap.put(key, apiModelAnno.order());
                // 扫描字段
                Field[] declaredFields = ClassUtil.getDeclaredFields(aClass);
                List<CustomFormDisplayDto> pushConfigDtoList = new ArrayList<>();
                for (Field field : declaredFields) {
                    CustomFormDisplayDto pushConfigDto = new CustomFormDisplayDto();
                    pushConfigDto.setField(field.getName());
                    pushConfigDto.setFieldType(field.getType().getSimpleName());
                    PushProperty pushProperty = AnnotationUtil.getAnnotation(field, PushProperty.class);
                    if (pushProperty != null) {
                        pushConfigDto.setName(pushProperty.value());
                        pushConfigDto.setDesc(pushProperty.desc() == null ? "请输入" + pushProperty.value() : pushProperty.desc());
                        pushConfigDto.setDefaultValue(pushProperty.defaultValue());
                        if (pushProperty.options() != null && pushProperty.options().length > 0) {
                            PushPropertyOptions[] options = pushProperty.options();
                            List<CustomFormDisplayOptions> optionsList = new ArrayList<>();
                            for (PushPropertyOptions option : options) {
                                CustomFormDisplayOptions configOptions = new CustomFormDisplayOptions();
                                configOptions.setValue(option.num());
                                configOptions.setName(option.name());
                                optionsList.add(configOptions);
                            }
                            pushConfigDto.setOptions(optionsList);
                        }
                        if (StrUtil.isNotBlank(pushProperty.ref())) {
                            pushConfigDto.setRef(pushProperty.ref());
                            pushConfigDto.setRefValue(pushProperty.refValue());
                        }
                    } else {
                        pushConfigDto.setName(field.getName());
                        pushConfigDto.setDesc("请输入" + field.getName());
                    }
                    pushConfigDtoList.add(pushConfigDto);
                }
                pushConfigMap.put(key, pushConfigDtoList);
            }
        }
        // 排序
        pushTypeList.sort(Comparator.comparingInt(orderMap::get));
        log.info("推送服务加载完成，共{}个", pushTypeList.size());
    }


    private ExecutorService createThread(String flag) {
        return Executors.newFixedThreadPool(1, r -> {
            Thread thread = new Thread(r);
            thread.setName("Push_Thread@" + flag);
            log.info("创建推送线程：" + thread.getName());
            return thread;
        });
    }
}
