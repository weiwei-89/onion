package org.edward.onion.tool;

import org.edward.onion.bind.annotation.Cut;
import org.edward.onion.bind.model.EnumInfo;
import org.edward.onion.bind.model.EnumInstance;
import org.edward.onion.bind.model.Peel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Knife {
    private static final int INIT_SIZE = 10;

    private Knife() {

    }

    public static Knife build() {
        return new Knife();
    }

    public Peel peel(Object target) throws Exception {
        Peel peel = new Peel();
        this.peel(target, peel);
        return peel;
    }

    private void peel(Object target, Peel peel) throws Exception {
        Field[] targetFields = target.getClass().getDeclaredFields();
        if(targetFields==null || targetFields.length==0) {
            return;
        }
        for(Field targetField : targetFields) {
            if(!targetField.isAnnotationPresent(Cut.class)) {
                continue;
            }
            Cut targetCut = targetField.getAnnotation(Cut.class);
            if(!targetCut.available()) {
                continue;
            }
            targetField.setAccessible(true);
            Object targetFieldValue = targetField.get(target);
            if(targetFieldValue == null) {
                continue;
            }
            if(Box.isPrimitive(targetFieldValue)) {
                if(targetCut.ignoreEmptyString()) {
                    if(targetFieldValue instanceof String && "".equals(targetFieldValue)) {
                        continue;
                    } else {
                        peel.put(targetField.getName(), targetFieldValue);
                    }
                } else {
                    peel.put(targetField.getName(), targetFieldValue);
                }
                if(targetCut.convert()) {
                    // TODO 添加缓存机制，避免多次读取同一个枚举类
                    EnumInstance enumInstance = Box.readEnumInstance(targetCut.convertDefination());
                    for(Map.Entry<String, Enum> entry : enumInstance.entrySet()) {
                        EnumInfo enumInfo = Box.getEnumInfo(entry.getValue());
                        if(enumInfo.get(targetCut.convertKey()).equals(String.valueOf(targetFieldValue))) {
                            peel.put(targetField.getName(), enumInfo.get(targetCut.convertValue()));
                            break;
                        }
                    }
                }
            } else if(targetFieldValue instanceof Iterable) {
                Iterable<?> targetList = (Iterable<?>) targetFieldValue;
                if(Box.isPrimitive(targetList)) {
                    peel.put(targetField.getName(), targetList);
                } else {
                    List<Peel> peelList = new ArrayList<>(INIT_SIZE);
                    for(Object targetItem : targetList) {
                        Peel aPeel = new Peel();
                        this.peel(targetItem, aPeel);
                        peelList.add(aPeel);
                    }
                    peel.put(targetField.getName(), peelList);
                }
            } else {
                Peel nextPeel = new Peel();
                peel.put(targetField.getName(), nextPeel);
                this.peel(targetFieldValue, nextPeel);
            }
        }
    }
}