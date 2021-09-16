package com.misec.utils;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * gson utils.
 *
 * @author Yellow
 * @since 2021-05-27 15:23
 */
public class GsonUtils {
    /**
     * 用于存储include字段和exclude字段的信息.
     */
    private static final ThreadLocal<Set<String>> THREAD_LOCAL = new ThreadLocal<>();
    /**
     * 默认的Gson序列化/反序列化对象.
     */
    private static final Gson DEFAULT_GSON = getDefaultGsonBuilder().create();
    /**
     * 打印格式化JSON的Gson序列化对象.
     */
    private static final Gson PRETTY_GSON = getDefaultGsonBuilder().setPrettyPrinting().create();
    /**
     * 排除指定字段的Gson序列化对象.
     */
    private static final Gson GSON_WITH_EXCLUDE = getDefaultGsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            Set<String> skipField = THREAD_LOCAL.get();
            return skipField != null && skipField.contains(f.getName());
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }).create();
    /**
     * 包含指定字段的Gson序列化对象.
     */
    private static final Gson GSON_WITH_INCLUDE = getDefaultGsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            Set<String> includeField = THREAD_LOCAL.get();
            return includeField == null || !includeField.contains(f.getName());
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }).create();

    /**
     * 获取默认的Gson Builder.
     */
    private static GsonBuilder getDefaultGsonBuilder() {
        return new GsonBuilder()
                // 关闭HTML特殊字符的转义
                .disableHtmlEscaping()
                // Date反序列化
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                    if (json == null) {
                        return null;
                    }
                    try {
                        JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
                        if (jsonPrimitive.isNumber()) {
                            return new Date(jsonPrimitive.getAsLong());
                        }
                        String dateString = jsonPrimitive.getAsString();
                        if (StringUtils.isNotBlank(dateString)) {
                            return LocalDateTimeUtils.parse(dateString);
                        }
                    } catch (Exception e) {
                        throw new JsonParseException(e);
                    }
                    return null;
                })
                // Date序列化
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) -> {
                    if (src == null) {
                        return JsonNull.INSTANCE;
                    } else {
                        return new JsonPrimitive(src.getTime());
                    }
                })
                // XMLGregorianCalendar反序列化
                .registerTypeAdapter(XMLGregorianCalendar.class, (JsonDeserializer<XMLGregorianCalendar>) (json, typeOfT, context) -> {
                    if (json == null) {
                        return null;
                    }
                    JsonObject jsonObject = json.getAsJsonObject();
                    try {
                        if (jsonObject.entrySet().isEmpty()) {
                            return null;
                        }
                        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
                        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar();
                        xmlGregorianCalendar.setYear(jsonObject.getAsJsonPrimitive("year").getAsInt());
                        xmlGregorianCalendar.setMonth(jsonObject.getAsJsonPrimitive("month").getAsInt());
                        xmlGregorianCalendar.setDay(jsonObject.getAsJsonPrimitive("day").getAsInt());
                        xmlGregorianCalendar.setHour(jsonObject.getAsJsonPrimitive("hour").getAsInt());
                        xmlGregorianCalendar.setMinute(jsonObject.getAsJsonPrimitive("minute").getAsInt());
                        xmlGregorianCalendar.setSecond(jsonObject.getAsJsonPrimitive("second").getAsInt());
                        xmlGregorianCalendar.setFractionalSecond(jsonObject.getAsJsonPrimitive("fractionalSecond").getAsBigDecimal());
                        xmlGregorianCalendar.setTimezone(jsonObject.getAsJsonPrimitive("timezone").getAsInt());
                        return xmlGregorianCalendar;
                    } catch (Exception e) {
                        throw new JsonParseException(e);
                    }
                })
                // XMLGregorianCalendar序列化
                .registerTypeAdapter(XMLGregorianCalendar.class, (JsonSerializer<XMLGregorianCalendar>) (src, typeOfSrc, context) -> {
                    if (src != null) {
                        JsonObject res = new JsonObject();
                        res.addProperty("year", src.getYear());
                        res.addProperty("month", src.getMonth());
                        res.addProperty("day", src.getDay());
                        res.addProperty("hour", src.getHour());
                        res.addProperty("minute", src.getMinute());
                        res.addProperty("second", src.getSecond());
                        res.addProperty("fractionalSecond", src.getFractionalSecond());
                        res.addProperty("timezone", src.getTimezone());
                        return res;
                    }
                    return JsonNull.INSTANCE;
                })
                ;
    }

    /**
     * 创建一个泛型参数type.
     *
     * @param raw  外层对象（List<T> Map<K,V> Result<T>等）
     * @param args 泛型对应的type
     * @return ParameterizedType
     */
    private static ParameterizedType make(Class<?> raw, Type[] args) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getRawType() {
                return raw;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    /**
     * 序列化对象成JSON字符串.
     *
     * @param source 对象
     * @return JSON字符串
     */
    public static String toJson(Object source) {
        return DEFAULT_GSON.toJson(source);
    }

    /**
     * 序列化对象成格式化的JSON字符串.
     *
     * @param source 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object source) {
        return PRETTY_GSON.toJson(source);
    }

    /**
     * 序列化对象成JSON字符串并过滤指定字段.
     *
     * @param source        对象
     * @param excludeFields 不需要序列化的字段
     * @return JSON字符串
     */
    public static String toJsonExclude(Object source, String... excludeFields) {
        try {
            THREAD_LOCAL.set(Sets.newHashSet(excludeFields));
            return GSON_WITH_EXCLUDE.toJson(source);
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    /**
     * 序列化对象指定的字段成JSON字符串.
     *
     * @param source        对象
     * @param includeFields 需要被序列化的字段
     * @return JSON字符串
     */
    public static String toJsonInclude(Object source, String... includeFields) {
        try {
            THREAD_LOCAL.set(Sets.newHashSet(includeFields));
            return GSON_WITH_INCLUDE.toJson(source);
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    /**
     * 把JSON字符串反序列化成对象.
     *
     * @param json   JSON字符串
     * @param tClass 反序列化对象的实体类
     * @param <T>    反序列化对象的实体类
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        return DEFAULT_GSON.fromJson(json, tClass);
    }

    /**
     * 把JSON字符串反序列化成对象.
     *
     * @param json JSON字符串
     * @param type 反序列化对象的映射{@link com.google.gson.reflect.TypeToken}
     * @param <T>  反序列化对象的实体类
     * @return 对象
     */
    public static <T> T fromJson(String json, Type type) {
        return DEFAULT_GSON.fromJson(json, type);
    }

    /**
     * 把JSON字符串反序列化成对象ArrayList.
     *
     * @param json   JSON字符串
     * @param tClass 反序列化对象的实体类
     * @param <T>    反序列化对象的实体类
     * @return 对象ArrayList
     */
    public static <T> List<T> fromArrayJson(String json, Class<T> tClass) {
        return DEFAULT_GSON.fromJson(json, make(ArrayList.class, new Class[]{tClass}));
    }

    /**
     * 把JSON字符串反序列化成HashMap对象（适合简单的kv Map使用）.
     * 复杂对象请定义一个class并使用{@link GsonUtils#fromJson(String, Class)}反序列化
     *
     * @param json   JSON字符串
     * @param kClass key实体类
     * @param vClass value实体类
     * @param <K>    key实体类
     * @param <V>    value实体类
     * @return 简单KV HashMap
     */
    public static <K, V> Map<K, V> fromMapJson(String json, Class<K> kClass, Class<V> vClass) {
        return DEFAULT_GSON.fromJson(json, make(HashMap.class, new Class[]{kClass, vClass}));
    }

    /**
     * 中国标准时间格式的Date转换适配器.
     * 可用于#{@link com.google.gson.annotations.JsonAdapter}覆盖默认的序列化/反序列化
     */
    public static class StandardDateTypeAdapter extends TypeAdapter<Date> {

        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(LocalDateTimeUtils.formatDateTime(value));
            }
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTimeUtils.parse(in.nextString());
        }
    }

}

