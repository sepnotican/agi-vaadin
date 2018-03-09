package com.sepnotican.springjpaformautocreator.generator.form.element;

import com.sepnotican.springjpaformautocreator.generator.annotations.Synonym;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;

@org.springframework.stereotype.Component
@Scope("stereotype")
public class AbstractElementForm<T> extends VerticalLayout {

    public static final String BTN_SAVE_TEXT = "Save";
    public static final String EMPTY_ENUM_TEXT = "<empty>";
    public static final String BTN_RELOAD_TEXT = "Reload";

    protected T entity;
    protected Binder binder;
    protected Layout defaultControlPanel;

    @Autowired
    private JpaRepository<T, Long> repository;

    public AbstractElementForm() {
    }

    public void init(T entity) {
        components.clear();
        this.entity = entity;
        binder = new Binder(entity.getClass());

        initDefaultControlPanel(binder);

        Class clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {

            Component component = getComponentByFieldAndBind(field, binder);

            if (component == null) continue;

            makeUpCaptionForField(field, component);
            addComponent(component);

        }
        binder.bindInstanceFields(entity);
        binder.readBean(entity);

    }

    protected void initDefaultControlPanel(Binder binder) {
        defaultControlPanel = new HorizontalLayout();

        Button buttonSave = new Button(BTN_SAVE_TEXT, event -> {
            try {
                binder.writeBean(entity);
                repository.save(entity);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
        buttonSave.setIcon(VaadinIcons.CHECK);

        Button buttonReload = new Button(BTN_RELOAD_TEXT, event -> {
            binder.readBean(entity);
        });
        buttonReload.setIcon(VaadinIcons.REFRESH);


        defaultControlPanel.addComponent(buttonSave);
        defaultControlPanel.addComponent(buttonReload);
        addComponent(defaultControlPanel);
    }

    protected void makeUpCaptionForField(Field field, Component component) {
        if (field.isAnnotationPresent(Synonym.class)) {
            component.setCaption(field.getAnnotation(Synonym.class).value());
        } else component.setCaption(field.getName());
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

        comboBox.setEmptySelectionCaption(EMPTY_ENUM_TEXT);

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
