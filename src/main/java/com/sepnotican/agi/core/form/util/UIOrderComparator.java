package com.sepnotican.agi.core.form.util;

import com.sepnotican.agi.core.annotations.AgiDrawOrder;

import java.lang.reflect.Field;
import java.util.Comparator;

public class UIOrderComparator implements Comparator<Field> {

    @Override
    public int compare(Field o1, Field o2) {
        if (!o1.isAnnotationPresent(AgiDrawOrder.class)
                && o2.isAnnotationPresent(AgiDrawOrder.class)) {
            if (0 < o2.getAnnotation(AgiDrawOrder.class).value()) return -1;
            if (0 > o2.getAnnotation(AgiDrawOrder.class).value()) return 1;
            else return 0;

        } else if (o1.isAnnotationPresent(AgiDrawOrder.class)
                && !o2.isAnnotationPresent(AgiDrawOrder.class)
                && o1.getAnnotation(AgiDrawOrder.class).value() > 0) {
            if (o1.getAnnotation(AgiDrawOrder.class).value() < 0) return -1;
            if (o1.getAnnotation(AgiDrawOrder.class).value() > 0) return 1;
            else return 0;
        } else if (!o1.isAnnotationPresent(AgiDrawOrder.class)
                && !o2.isAnnotationPresent(AgiDrawOrder.class)) return 0;
        else if (o1.getAnnotation(AgiDrawOrder.class).value() >
                o2.getAnnotation(AgiDrawOrder.class).value()) return 1;
        else if ((o1.getAnnotation(AgiDrawOrder.class).value() <
                o2.getAnnotation(AgiDrawOrder.class).value())) return -1;
        else return 0;
    }
}