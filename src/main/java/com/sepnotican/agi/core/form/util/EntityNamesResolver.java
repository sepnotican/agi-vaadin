package com.sepnotican.agi.core.form.util;

import com.sepnotican.agi.core.annotations.AgiEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityNamesResolver {

    public String getManyName(Class aClass) {
        AgiEntity agiEntity = (AgiEntity) aClass.getAnnotation(AgiEntity.class);
        return agiEntity.manyCaption().isEmpty() ? aClass.getName() : agiEntity.manyCaption();
    }

}
