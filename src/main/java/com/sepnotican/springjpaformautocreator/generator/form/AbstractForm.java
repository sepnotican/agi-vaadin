package com.sepnotican.springjpaformautocreator.generator.form;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;

@org.springframework.stereotype.Component
public class AbstractForm<T> extends VerticalLayout {

    private static final String BTN_SAVE_TEXT = "Save";
    private T entity;

    @Autowired
    private JpaRepository<T, Long> repository;

    public AbstractForm() {
    }

    public void init(T entity) {
        this.entity = entity;
        components.clear();

        Class clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Binder binder = new Binder(entity.getClass());

        for (Field field : fields) {

            Component component = getComponentByFieldAndBind(field, binder);

            if (component == null) continue;

            addComponent(component);

        }
        binder.bindInstanceFields(entity);
        binder.readBean(entity);

        Button buttonSave = new Button(BTN_SAVE_TEXT, event -> {
            try {
                binder.writeBean(entity);
                repository.save(entity);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        addComponent(buttonSave);
    }

    protected Component getComponentByFieldAndBind(Field field, Binder binder) {
        if (field.getType().equals(Long.class)) {
            return generateLongField(field, binder);

        } else if (field.getType().equals(String.class)) {
            return generateStringField(field, binder);

        } else if (field.getType().isEnum()) {
            return generateEnumField(field, binder);

        } else return null;
    }

    protected Component generateEnumField(Field field, Binder binder) {

        Class clazzEnum = field.getType();
        ComboBox comboBox = new ComboBox<>();
        comboBox.setEmptySelectionCaption(clazzEnum.getName());
        comboBox.setItems(field.getType().getEnumConstants());

        binder.bind(comboBox, field.getName());

        return comboBox;
    }

    protected Component generateStringField(Field field, Binder binder) {
        TextField textField = new TextField(field.getName());

        binder.bind(textField, field.getName());

        return textField;
    }

    protected Component generateLongField(Field field, Binder binder) {
        TextField textField = new TextField(field.getName());

        binder.forField(textField)
                .withConverter(new StringToLongConverter("Must be a Long value"))
                .bind(field.getName());

        if (field.isAnnotationPresent(javax.persistence.Id.class))
            textField.setReadOnly(true);

        return textField;
    }


}
