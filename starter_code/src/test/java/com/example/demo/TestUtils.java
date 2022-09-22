package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            // Check the accessibility
            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }
            // Set the objected to be injected into the desired field
            field.set(target, toInject);

            //Restore the accessibility
            if (!wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
