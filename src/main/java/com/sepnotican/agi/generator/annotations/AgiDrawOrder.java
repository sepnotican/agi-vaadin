package com.sepnotican.agi.generator.annotations;

import com.sepnotican.agi.generator.form.generic.AbstractElementForm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation uses in {@link AbstractElementForm}
 * when creating fields
 * to define to specific coordinates in the grid
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AgiDrawOrder {
    int drawOrder() default 0;

}