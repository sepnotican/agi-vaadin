package com.sepnotican.springjpaformautocreator.generator.annotations;

import com.vaadin.icons.VaadinIcons;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation points to create main menu with annotated element
 * in {@link com.sepnotican.springjpaformautocreator.generator.form.MainMenuGenerator}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateUI {
    String caption();

    VaadinIcons icon() default VaadinIcons.FILE_PICTURE;

    Class repo();
}
