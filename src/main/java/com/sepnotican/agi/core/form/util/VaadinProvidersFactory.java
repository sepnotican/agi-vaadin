package com.sepnotican.agi.core.form.util;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.ItemCaptionGenerator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class VaadinProvidersFactory {

    public static <T> ValueProvider<T, String> getValueProvider(Field field, String methodName) {
        final Method method = findMethodByName(field, methodName);
        return (ValueProvider<T, String>) anObject -> {
            try {
                Object classMember;
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                    classMember = field.get(anObject);
                    field.setAccessible(false);
                } else classMember = field.get(anObject);
                if (classMember != null) return String.valueOf(method.invoke(classMember));
                else return "";
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        };
    }

    public static <T, F> ValueProvider<T, F> getValueProvider(Method method) {
        try {
            return (ValueProvider<T, F>) method.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public static <T> ItemCaptionGenerator<T> getItemCaptionGenerator(Field field, String methodName) {
        final Method foundMethod = findMethodByName(field, methodName);
        return (ItemCaptionGenerator<T>) anObject -> {
            try {
                return String.valueOf(foundMethod.invoke(anObject));
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage());
            }
            return null;
        };
    }

    protected static Method findMethodByName(Field field, String methodName) {
        for (Method declaredMethod : field.getType().getDeclaredMethods()) {
            if (declaredMethod.getName().equals(methodName)) return declaredMethod;
        }
        RepresentationResolverExecption execption = new RepresentationResolverExecption(
                String.format("Can't find method=%s for field=%s", methodName, field.getName()));
        log.error("Method with annotation RepresentationResolver not found in class " + field.getType().getCanonicalName(), execption);
        throw execption;
    }

}
