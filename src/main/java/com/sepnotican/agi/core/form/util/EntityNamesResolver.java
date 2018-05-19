package com.sepnotican.agi.core.form.util;

import com.sepnotican.agi.core.annotations.AgiUI;
import org.springframework.stereotype.Component;

@Component
public class EntityNamesResolver {

    public String getManyName(Class aClass) {
        AgiUI agiUI = (AgiUI) aClass.getAnnotation(AgiUI.class);
        return agiUI.manyCaption().isEmpty() ? aClass.getName() : agiUI.manyCaption();
    }

}
