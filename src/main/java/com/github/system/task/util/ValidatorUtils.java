package com.github.system.task.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.push.base.annotation.PushProperty;
import com.github.system.base.util.SpringUtil;
import com.github.system.task.annotation.SettingColumn;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.Set;


/**
 * Validator 校验框架工具
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatorUtils {

    private static final Validator VALID = SpringUtil.getBean(Validator.class);

    public static <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> validate = VALID.validate(object, groups);
        if (!validate.isEmpty()) {
            throw new ConstraintViolationException("参数校验异常", validate);
        }
    }

    public static String parseHtmlError(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder sb = new StringBuilder("<div>参数校验失败：");
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            Class<?> rootBeanClass = constraintViolation.getRootBeanClass();
            sb.append("<p style='margin-top: 5px'>字段 <strong style='color: teal;'>");
            String fieldName = constraintViolation.getPropertyPath().toString();
            Field field = ReflectUtil.getField(rootBeanClass, fieldName);
            if (field != null) {
                SettingColumn settingColumn = AnnotationUtil.getAnnotation(field, SettingColumn.class);
                PushProperty pushProperty = AnnotationUtil.getAnnotation(field, PushProperty.class);
                if (settingColumn != null) {
                    fieldName = settingColumn.name();
                } else if (pushProperty != null) {
                    fieldName = pushProperty.value();
                }
            }
            sb.append(fieldName).append(" </strong>").append(constraintViolation.getMessage()).append("</p>");
        }
        return sb + "</div>";
    }

}

