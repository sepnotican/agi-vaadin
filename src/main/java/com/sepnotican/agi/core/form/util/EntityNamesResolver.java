package com.sepnotican.agi.core.form.util;

import com.sepnotican.agi.core.annotations.AgiEntity;
import com.sepnotican.agi.core.annotations.AgiForm;
import org.springframework.stereotype.Component;

@Component
public class EntityNamesResolver {

    public String getMenuName(Class aClass) {
        if (aClass.isAnnotationPresent(AgiEntity.class)) {
            AgiEntity agiEntity = (AgiEntity) aClass.getAnnotation(AgiEntity.class);
            return agiEntity.menuCaption().isEmpty() ? aClass.getName() : agiEntity.menuCaption();
        } else if (aClass.isAnnotationPresent(AgiForm.class)) {
            AgiForm agiForm = (AgiForm) aClass.getAnnotation(AgiForm.class);
            return agiForm.caption().isEmpty() ? aClass.getName() : agiForm.caption();
        }
        return "void";
    }

}
