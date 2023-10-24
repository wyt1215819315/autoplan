package com.github.system.desensitized;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.github.system.task.annotation.DataDesensitization;

import java.io.IOException;

public class DesensitizedJsonSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    private DataDesensitization desensitization;

    @Override
    public void serialize(Object s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(DataDesensitizationUtil.desensitizationValue(desensitization, s));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {

        desensitization = beanProperty.getAnnotation(DataDesensitization.class);

        if (!ObjectUtils.isEmpty(desensitization) && String.class.isAssignableFrom(beanProperty.getType().getRawClass())) {
            return this;
        }
        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
}