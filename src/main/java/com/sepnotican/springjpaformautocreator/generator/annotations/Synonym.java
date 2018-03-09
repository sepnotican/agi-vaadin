package com.sepnotican.springjpaformautocreator.generator.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * annotation uses in {@link com.sepnotican.springjpaformautocreator.generator.form.AbstractForm}
 * when creating fields
 * to create user-friendly captions to the fields.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Synonym {
    String value();
}
