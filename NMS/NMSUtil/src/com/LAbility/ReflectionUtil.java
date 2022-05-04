package com.LAbility;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Optional;

public class ReflectionUtil {
    @Nullable
    public static Object getPrivateObject(Object object, String fieldName) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch(IllegalAccessException ignore) {}
        return null;
    }

    public static int getPrivateInteger(Object object, String fieldName) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        try {
            return field.getInt(object);
        } catch(IllegalAccessException ignore) {}
        return 0;
    }

    public static int getPrivateOptionalInteger(Object object, String fieldName) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        try {
            Optional<?> value = (Optional<?>) field.get(object);
            if(value.isPresent()) {
                return ((Optional<Integer>) field.get(object)).get().intValue();
            }
        } catch(IllegalAccessException ignore) {}
        return 0;
    }
}