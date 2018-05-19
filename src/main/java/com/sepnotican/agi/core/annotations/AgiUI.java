package com.sepnotican.agi.core.annotations;

import com.sepnotican.agi.core.form.MainMenuGenerator;
import com.vaadin.icons.VaadinIcons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation points to create main menu with annotated element
 * in {@link MainMenuGenerator}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AgiUI {
    String singleCaption();

    String manyCaption();
    VaadinIcons icon() default VaadinIcons.FILE_PICTURE;

    String synonymField();
}
