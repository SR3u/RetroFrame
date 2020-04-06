package org.sr3u.photoframe.settings;

import java.lang.reflect.Field;
import java.util.Properties;

public interface Fillable {
    default void fill(Properties properties) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(PropertyMap.class)) {
                field.setAccessible(true);
                PropertyMap annotation = field.getAnnotation(PropertyMap.class);
                String propertyName = annotation.value();
                try {
                    String s = (String) properties.get(propertyName);
                    if (s != null) {
                        Class<?> type = field.getType();
                        if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                            field.set(this, "true".equalsIgnoreCase(s));
                        } else if (String.class.equals(type)) {
                            field.set(this, s);
                        } else if (Integer.class.equals(type) || int.class.equals(type)) {
                            field.set(this, Integer.valueOf(s));
                        } else if (Long.class.equals(type) || long.class.equals(type)) {
                            field.set(this, Long.valueOf(s));
                        } else if (Double.class.equals(type) || double.class.equals(type)) {
                            field.set(this, Double.valueOf(s));
                        } else {
                            throw new Exception("Unsupported type: " + type);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Failed to fill property " + getClass().getName() + "." + field.getName() + " with setting " + propertyName);
                }
            }
        }
    }

    default Properties toProperties() {
        Properties properties = new Properties();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(PropertyMap.class)) {
                field.setAccessible(true);
                PropertyMap annotation = field.getAnnotation(PropertyMap.class);
                try {
                    properties.setProperty(annotation.value(), String.valueOf(field.get(this)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return properties;
    }
}
