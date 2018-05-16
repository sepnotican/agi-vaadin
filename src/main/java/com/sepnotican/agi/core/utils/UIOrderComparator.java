package com.sepnotican.agi.core.utils;

import com.sepnotican.agi.core.annotations.AgiDrawOrder;

import java.lang.reflect.Field;
import java.util.Comparator;

public class UIOrderComparator implements Comparator<Field> {

    @Override
    public int compare(Field o1, Field o2) {
        if (!o1.isAnnotationPresent(AgiDrawOrder.class)
                && o2.isAnnotationPresent(AgiDrawOrder.class))
            return 1;
        else if (o1.isAnnotationPresent(AgiDrawOrder.class)
                && !o2.isAnnotationPresent(AgiDrawOrder.class))
            return -1;
        else if (!o1.isAnnotationPresent(AgiDrawOrder.class)
                && !o2.isAnnotationPresent(AgiDrawOrder.class))
            return 0;
        else if (o1.getAnnotation(AgiDrawOrder.class).drawOrder() >
                o2.getAnnotation(AgiDrawOrder.class).drawOrder())
            return 1;
        else if ((o1.getAnnotation(AgiDrawOrder.class).drawOrder() <
                o2.getAnnotation(AgiDrawOrder.class).drawOrder()))
            return -1;

        return 0;
    }
}