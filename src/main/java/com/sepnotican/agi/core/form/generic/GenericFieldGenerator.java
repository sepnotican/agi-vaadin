package com.sepnotican.agi.core.form.generic;

import com.sepnotican.agi.core.annotations.AgiUI;
import com.sepnotican.agi.core.annotations.BigString;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class GenericFieldGenerator {
    @Value("${agi.forms.element.enum-null-selection}")
    protected String EMPTY_ENUM_TEXT;
    @Autowired
    private Logger logger;
    @Autowired
    private ApplicationContext context;


    protected com.vaadin.ui.Component getComponentByFieldAndBind(Field field, Binder binder) {
        if (field.getType().equals(Long.class)
                || field.getType().equals(long.class)) {
            return generateLongField(field, binder);

        } else if (field.getType().equals(Double.class)
                || field.getType().equals(double.class)) {
            return generateDoubleFieild(field, binder);

        } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
            com.vaadin.ui.Component component = generateFloatFieild(field);
            binder.forField((HasValue) component)
                    .withConverter(new StringToFloatConverter("Must be a float value"))
                    .bind(field.getName());
            return component;
        } else if (field.getType().equals(String.class)) {
            return generateStringField(field, binder);

        } else if (field.getType().isEnum()) {
            return generateEnumField(field, binder);

        } else return generateLinkedObjectField(field, binder);
    }

    protected com.vaadin.ui.Component generateFloatFieild(Field field) {
        return new TextField(field.getName());
    }

    protected com.vaadin.ui.Component generateDoubleFieild(Field field, Binder binder) {
        TextField textField = new TextField(field.getName());
        binder.forField(textField)
                .withConverter(new StringToDoubleConverter("Must be a double value"))
                .bind(field.getName());
        return textField;
    }

    protected com.vaadin.ui.Component generateEnumField(Field field, Binder binder) {
        ComboBox comboBox = new ComboBox<>();
        comboBox.setEmptySelectionCaption(EMPTY_ENUM_TEXT);
        comboBox.setItems(field.getType().getEnumConstants());
        binder.bind(comboBox, field.getName());
        return comboBox;
    }

    protected com.vaadin.ui.Component generateStringField(Field field, Binder binder) {

        com.vaadin.ui.Component textField = null;
        if (field.isAnnotationPresent(BigString.class)) {
            textField = new TextArea();
        } else textField = new TextField();
        binder.bind((HasValue) textField, field.getName());
        return textField;
    }

    protected com.vaadin.ui.Component generateLongField(Field field, Binder binder) {
        TextField textField = new TextField(field.getName());
        binder.forField(textField)
                .withConverter(new StringToLongConverter("Must be a Long value"))
                .bind(field.getName());
        return textField;
    }

    protected com.vaadin.ui.Component generateLinkedObjectField(Field field, Binder binder) {

        if (!field.isAnnotationPresent(LinkedObject.class)) return null;
        if (!field.getType().isAnnotationPresent(AgiUI.class)) {
            logger.warn("Attempt to create field without AgiUI annotation. " +
                    "\nClassname = " + field.getClass().getCanonicalName() +
                    "\nField name = " + field.getName());
            return null;
        }

        Class repositoryClass = field.getType().getAnnotation(AgiUI.class).repo();
        JpaRepository repository = (JpaRepository) context.getBean(repositoryClass);

        ComboBox comboBox = new ComboBox<>();
        comboBox.setEmptySelectionCaption(EMPTY_ENUM_TEXT);
        comboBox.setItems(repository.findAll());

        binder.bind(comboBox, field.getName());
        return comboBox;
    }
}
