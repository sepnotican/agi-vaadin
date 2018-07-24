package agi.core.annotations;

import agi.core.form.generic.AbstractListForm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Provides a ValueProvider for {@link AbstractListForm}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgiColumnValueProvider {
}
