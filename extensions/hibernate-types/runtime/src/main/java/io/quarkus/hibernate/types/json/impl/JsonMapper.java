package io.quarkus.hibernate.types.json.impl;

import java.lang.reflect.Type;

public interface JsonMapper {

    Class<?> getBinaryTypeClass();

    <T> T fromJson(String string, Class<T> clazz);

    <T> T fromJson(String string, Type type);

    String toJson(Object value);

    Object readObject(String value);

    <T> T clone(T value);
}
