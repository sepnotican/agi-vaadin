package com.sepnotican.springjpaformautocreator.generator.annotations;

import com.sepnotican.springjpaformautocreator.generator.form.AbstractElementForm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * annotation uses in {@link AbstractElementForm}
 * when creating fields
 * to create user-friendly captions to the fields.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Synonym {
    String value();
}
