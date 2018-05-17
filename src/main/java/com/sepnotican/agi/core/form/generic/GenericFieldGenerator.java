package com.sepnotican.agi.core.form.generic;

import com.sepnotican.agi.core.annotations.AgiUI;
import com.sepnotican.agi.core.annotations.BigString;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.core.utils.RepositoryDataProvider;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@Slf4j
public class GenericFieldGenerator {
    @Value("${agi.forms.element.enum-null-selection}")
    protected String EMPTY_ENUM_TEXT;
    @Autowired
    private ApplicationContext context;

    protected com.vaadin.ui.Component getComponentByField(Field field) {
        com.vaadin.ui.Component component;
        if (field.getType().equals(Long.class)
                || field.getType().equals(long.class)) {
            component = generateLongField(field);
        } else if (field.getType().equals(Double.class)
                || field.getType().equals(double.class)) {
            component = generateDoubleFieild(field);
        } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
            component = generateFloatFieild(field);
        } else if (field.getType().equals(String.class)) {
            component = generateStringField(field);
        } else if (field.getType().isEnum()) {
            component = generateEnumField(field);
        } else if (field.isAnnotationPresent(LinkedObject.class)) {
            component = generateLinkedObjectField(field);
        } else {
            log.error("getComponentByField(): not implemented cast for {}", field.getType().getCanonicalName());
            return null;
        }
        makeUpCaptionForField(field, component);
        return component;
    }

    protected com.vaadin.ui.Component getComponentByFieldAndBind(Field field, Binder binder) {
        com.vaadin.ui.Component component = getComponentByField(field);
        if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
            binder.forField((HasValue) component)
                    .withConverter(new StringToLongConverter("Must be a Long value"))
                    .bind(field.getName());
        } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
            binder.forField((HasValue) component)
                    .withConverter(new StringToDoubleConverter("Must be a double value"))
                    .bind(field.getName());
        } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
            binder.forField((HasValue) component)
                    .withConverter(new StringToFloatConverter("Must be a float value"))
                    .bind(field.getName());
        } else if (field.getType().equals(String.class)
                || field.getType().isEnum()
                || field.isAnnotationPresent(LinkedObject.class)) {
            binder.bind((HasValue) component, field.getName());
        } else {
            log.error("getComponentByFieldAndBind(): not implemented cast for {}", field.getType().getCanonicalName());
            return null;
        }
        return component;
    }

    protected com.vaadin.ui.Component generateFloatFieild(Field field) {
        return new TextField(field.getName());
    }

    protected com.vaadin.ui.Component generateDoubleFieild(Field field) {
        return new TextField(field.getName());
    }

    protected com.vaadin.ui.Component generateEnumField(Field field) {
        ComboBox comboBox = new ComboBox<>();
        comboBox.setEmptySelectionCaption(EMPTY_ENUM_TEXT);
        comboBox.setItems(field.getType().getEnumConstants());
        return comboBox;
    }

    protected com.vaadin.ui.Component generateStringField(Field field) {
        com.vaadin.ui.Component textField = null;
        if (field.isAnnotationPresent(BigString.class)) {
            textField = new TextArea();
        } else textField = new TextField();
        return textField;
    }

    protected com.vaadin.ui.Component generateLongField(Field field) {
        return new TextField(field.getName());
    }

    protected com.vaadin.ui.Component generateLinkedObjectField(Field field) {
        if (!field.getType().isAnnotationPresent(AgiUI.class)) {
            log.error("Attempt to create field without AgiUI annotation. " +
                    "Classname = " + field.getClass().getCanonicalName() +
                    "Field name = " + field.getName());
            return null;
        }
        Class repositoryClass = field.getType().getAnnotation(AgiUI.class).repo();
        JpaRepository repository = (JpaRepository) context.getBean(repositoryClass);
        ComboBox comboBox = new ComboBox<>();
        if (field.isAnnotationPresent(Synonym.class)) {
            comboBox.setCaption(field.getAnnotation(Synonym.class).value());
        } else {
            comboBox.setCaption(field.getName());
        }
        comboBox.setEmptySelectionCaption(EMPTY_ENUM_TEXT);
        comboBox.setDataProvider(new RepositoryDataProvider<>(repository));

        return comboBox;
    }

    protected void makeUpCaptionForField(Field field, com.vaadin.ui.Component component) {
        if (field.isAnnotationPresent(Synonym.class)) {
            component.setCaption(field.getAnnotation(Synonym.class).value());
        } else component.setCaption(field.getName());
    }

    protected void makeUpCaptionForField(Field field, Grid.Column column) {
        if (field.isAnnotationPresent(Synonym.class)) {
            column.setCaption(field.getAnnotation(Synonym.class).value());
        } else column.setCaption(field.getName());
    }
}
