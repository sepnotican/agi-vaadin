package com.sepnotican.agi.core.annotations;

import com.sepnotican.agi.core.form.generic.AbstractElementForm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation uses in {@link AbstractElementForm}
 * points to create big multi-line text area
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BigString {
}
