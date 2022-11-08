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
        if(target instanceof Map) {
            Map<String, Object> targetMap = (Map<String, Object>) target;
            Peel firstPeel = new Peel(targetMap.size());
            for(Map.Entry<String, Object> entry : targetMap.entrySet()) {
                Peel peel = new Peel();
                this.peel(entry.getValue(), peel);
                firstPeel.put(entry.getKey(), peel);
            }
            return firstPeel;
        } else {
            Peel peel = new Peel();
            this.peel(target, peel);
            return peel;
        }
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
                        // TODO 当tag为空时，默认使用成员变量的名称
                        peel.put(targetCut.tag(), targetFieldValue);
                    }
                } else {
                    peel.put(targetCut.tag(), targetFieldValue);
                }
                if(targetCut.convert()) {
                    // TODO 添加缓存机制，避免多次读取同一个枚举类
                    EnumInstance enumInstance = Box.readEnumInstance(targetCut.convertDefination());
                    for(Map.Entry<String, Enum> entry : enumInstance.entrySet()) {
                        EnumInfo enumInfo = Box.getEnumInfo(entry.getValue());
                        if(enumInfo.get(targetCut.convertKey()).equals(String.valueOf(targetFieldValue))) {
                            peel.put(targetCut.tag(), enumInfo.get(targetCut.convertValue()));
                            break;
                        }
                    }
                }
            } else if(targetFieldValue instanceof Iterable) {
                Iterable<?> targetList = (Iterable<?>) targetFieldValue;
                if(Box.isPrimitive(targetList)) {
                    peel.put(targetCut.tag(), targetList);
                } else {
                    // TODO 正确获取列表的长度，而不是无脑设置为一个估计值(参照下面Map的处理方式)
                    List<Peel> peelList = new ArrayList<>(INIT_SIZE);
                    for(Object targetItem : targetList) {
                        Peel aPeel = new Peel();
                        this.peel(targetItem, aPeel);
                        peelList.add(aPeel);
                    }
                    peel.put(targetCut.tag(), peelList);
                }
            } else if(targetFieldValue instanceof Map) {
                Map<String, Object> targetFieldMap = (Map<String, Object>) targetFieldValue;
                Peel mapPeel = new Peel(targetFieldMap.size());
                for(Map.Entry<String, Object> entry : targetFieldMap.entrySet()) {
                    Object targetFieldMapValue = entry.getValue();
                    if(Box.isPrimitive(targetFieldMapValue)) {
                        mapPeel.put(entry.getKey(), String.valueOf(targetFieldMapValue));
                    } else {
                        Peel aPeel = new Peel();
                        this.peel(entry.getValue(), aPeel);
                        mapPeel.put(entry.getKey(), aPeel);
                    }
                }
                peel.put(targetCut.tag(), mapPeel);
            } else {
                Peel nextPeel = new Peel();
                peel.put(targetCut.tag(), nextPeel);
                this.peel(targetFieldValue, nextPeel);
            }
        }
    }
}