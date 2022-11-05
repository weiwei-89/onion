package org.edward.onion.tool;

import org.edward.onion.bind.model.EnumInfo;
import org.edward.onion.bind.model.EnumInstance;

import java.lang.reflect.Field;

public class Box {
    public static boolean isPrimitive(Object target) {
        if(target instanceof String) {
            return true;
        }
        if(target instanceof Number) {
            return true;
        }
        if(target instanceof Boolean) {
            return true;
        }
        if(target instanceof Character) {
            return true;
        }
        return target.getClass().isPrimitive();
    }

    public static boolean isPrimitive(Iterable<?> objectList) {
        return isPrimitive(objectList.iterator().next());
    }

    public static EnumInstance readEnumInstance(Class<?> targetClass) throws Exception {
        Field[] targetFields = targetClass.getDeclaredFields();
        if(targetFields==null || targetFields.length==0) {
            return null;
        }
        EnumInstance enumInstance = new EnumInstance();
        for(Field targetField : targetFields) {
            if(!targetField.isEnumConstant()) {
                continue;
            }
            enumInstance.put(targetField.getName(), (Enum)targetField.get(targetClass));
        }
        return enumInstance;
    }

    public static EnumInfo getEnumInfo(Enum target) throws Exception {
        Field[] targetFields = target.getClass().getDeclaredFields();
        if(targetFields==null || targetFields.length==0) {
            return null;
        }
        EnumInfo enumInfo = new EnumInfo();
        for(Field targetField : targetFields) {
            targetField.setAccessible(true);
            Object targetFieldValue = targetField.get(target);
            if(targetFieldValue instanceof String) {
                enumInfo.put(targetField.getName(), (String)targetFieldValue);
            }
        }
        return enumInfo;
    }
}