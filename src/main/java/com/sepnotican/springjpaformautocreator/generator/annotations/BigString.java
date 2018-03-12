package com.sepnotican.springjpaformautocreator.generator.annotations;

import com.sepnotican.springjpaformautocreator.generator.form.generic.AbstractElementForm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * annotation uses in {@link AbstractElementForm}
 * points to create big multi-line text area
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BigString {
}
