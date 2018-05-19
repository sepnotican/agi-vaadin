package com.sepnotican.agi.core.form.util;

import com.sepnotican.agi.core.annotations.RepresentationResolver;
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
                if (classMember != null) return (String) method.invoke(classMember);
                return (String) method.invoke(anObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage());
            }
            return null;
        };
    }


    public static <T> ItemCaptionGenerator<T> getItemCaptionGenerator(Field field, String methodName) {
        final Method foundMethod = findMethodByName(field, methodName);
        return (ItemCaptionGenerator<T>) anObject -> {
            try {
                return (String) foundMethod.invoke(anObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage());
            }
            return null;
        };
    }

    protected static Method findMethodByName(Field field, String methodName) {
        for (Method declaredMethod : field.getType().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(RepresentationResolver.class) &&
                    declaredMethod.getAnnotation(RepresentationResolver.class).value().equals(methodName)) {
                return declaredMethod;
            }
        }
        RepresentationResolverExecption execption = new RepresentationResolverExecption("Can't find method=" + methodName);
        log.error("Method with annotation RepresentationResolver not found in class " + field.getType().getCanonicalName(), execption);
        throw execption;
    }

}
