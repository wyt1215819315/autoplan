package com.github.system.base.util;

import cn.hutool.core.annotation.AnnotationUtil;
import com.github.system.base.constant.SystemConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SpringUtil extends cn.hutool.extra.spring.SpringUtil {

    private final static String RESOURCE_PATTERN = "/**/*.class";

    public static String getApplicationPath() {
        ApplicationHome ah = new ApplicationHome(SpringUtil.class);
        return ah.getSource().getParentFile().toString();
    }

    /**
     * 优先获取SpringBean，如果SpringBean为空则获取普通java实例
     */
    public static <T> T getBeanOrInstance(Class<T> clazz) {
        T bean = null;
        try {
            bean = getBean(clazz);
        } catch (Exception ignored) {}
        if (bean == null) {
            try {
                bean = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }
        return bean;
    }

    public static <A extends Annotation> List<A> scanAllAnnotationByAnnotation(Class<A> annotationType) {
        List<A> aList = new ArrayList<>();
        //spring工具类，可以获取指定路径下的全部类
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemConstant.BASE_PACKAGE) + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            //MetadataReader 的工厂类
            MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                //用于读取类信息
                MetadataReader reader = readerfactory.getMetadataReader(resource);
                //扫描到的class
                String classname = reader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(classname);
                //判断是否有指定主解
                A annotation = clazz.getAnnotation(annotationType);
                if (annotation != null) {
                    //将注解中的类型值作为key，对应的类作为 value
                    aList.add(annotation);
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    A a = AnnotationUtil.getAnnotation(method, annotationType);
                    if (a != null) {
                        aList.add(a);
                    }
                }
            }
            return aList;
        } catch (IOException | ClassNotFoundException e) {
            log.error("包扫描时出错：" + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

}
