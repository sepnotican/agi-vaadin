package com.sepnotican.springjpaformautocreator.generator.annotations;

import com.sepnotican.springjpaformautocreator.generator.form.element.AbstractElementForm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * annotation uses in {@link AbstractElementForm}
 * when creating fields
 * to define to specific coordinates in the grid
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UIDrawOrder {
    int row();

    int rowStretch();

    int column();

    int columnStretch();

}
