package com.sepnotican.agi.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Provides a ValueProvider for {@link com.sepnotican.agi.core.form.generic.AbstractListForm}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgiColumnValueProvider {
    String value();
}
