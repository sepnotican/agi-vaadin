package com.sepnotican.springjpaformautocreator.generator.form.element;

import com.sepnotican.springjpaformautocreator.generator.annotations.BigString;
import com.sepnotican.springjpaformautocreator.generator.annotations.Synonym;
import com.sepnotican.springjpaformautocreator.generator.annotations.UIDrawOrder;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
@Scope("stereotype")
public class AbstractElementForm<T> extends GridLayout {

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
        removeAllComponents();

        this.entity = entity;
        binder = new Binder(entity.getClass());

        initDefaultControlPanel(binder);

        Class clazz = entity.getClass();
        Field[] fieldsArray = clazz.getDeclaredFields();
        ArrayList<Field> fieldArrayList = createOrderedElementsList(fieldsArray);

        drawGridRowsColumnsByArray(fieldArrayList);

        for (Field field : fieldArrayList) {

            Component component = getComponentByFieldAndBind(field, binder);

            if (component == null) continue;

            component.setWidthUndefined();
            component.setHeightUndefined();
            component.setSizeFull();

            makeUpCaptionForField(field, component);

            if (field.isAnnotationPresent(UIDrawOrder.class)) {
                UIDrawOrder uiDrawOrder = field.getAnnotation(UIDrawOrder.class);
                System.err.println("" + field.getName() + " " +
                        uiDrawOrder.column() + " " +
                        uiDrawOrder.row() + " " +
                        uiDrawOrder.columnStretch() + " " +
                        uiDrawOrder.rowStretch());
                addComponent(component,
                        uiDrawOrder.column(),
                        uiDrawOrder.row(),
                        uiDrawOrder.columnStretch(),
                        uiDrawOrder.rowStretch());
            } else addComponent(component);

        }
        binder.bindInstanceFields(entity);
        binder.readBean(entity);

    }

    protected void drawGridRowsColumnsByArray(ArrayList<Field> fieldArrayList) {
        int columns = 1, rows = 0;
        for (Field field : fieldArrayList) {
            if (field.isAnnotationPresent(UIDrawOrder.class)) {
                UIDrawOrder uiDrawOrder = field.getAnnotation(UIDrawOrder.class);
                columns = Math.max(uiDrawOrder.column() + uiDrawOrder.columnStretch(), columns);
                rows = Math.max(uiDrawOrder.row() + uiDrawOrder.rowStretch(), rows);
            }
        }
        rows += 1;
        columns += 1;
        setRows(rows == 0 ? fieldArrayList.size() : rows);
        setColumns(columns);
    }

    protected ArrayList<Field> createOrderedElementsList(Field[] fieldsArray) {
        return new ArrayList<>(Arrays.stream(fieldsArray)
                .sorted(new UIOrderColumnRowComparator())
                .collect(Collectors.toList()));
    }

    protected class UIOrderColumnRowComparator implements Comparator<Field> {

        @Override
        public int compare(Field o1, Field o2) {
            if (!o1.isAnnotationPresent(UIDrawOrder.class)
                    && o2.isAnnotationPresent(UIDrawOrder.class))
                return 1;
            else if (o1.isAnnotationPresent(UIDrawOrder.class)
                    && !o2.isAnnotationPresent(UIDrawOrder.class))
                return -1;
            else if (!o1.isAnnotationPresent(UIDrawOrder.class)
                    && !o2.isAnnotationPresent(UIDrawOrder.class))
                return 0;
            else if (o1.getAnnotation(UIDrawOrder.class).column() >
                    o2.getAnnotation(UIDrawOrder.class).column())
                return 1;
            else if ((o1.getAnnotation(UIDrawOrder.class).column() <
                    o2.getAnnotation(UIDrawOrder.class).column()))
                return -1;

            return compareByRow(o1, o2);
        }

        private int compareByRow(Field o1, Field o2) {
            if (!o1.isAnnotationPresent(UIDrawOrder.class))
                return -1;
            if (o1.isAnnotationPresent(UIDrawOrder.class)
                    && o2.isAnnotationPresent(UIDrawOrder.class)) {
                if (o1.getAnnotation(UIDrawOrder.class).row() >
                        o2.getAnnotation(UIDrawOrder.class).row())
                    return 1;
                else if ((o1.getAnnotation(UIDrawOrder.class).row() <
                        o2.getAnnotation(UIDrawOrder.class).row()))
                    return -1;
                else return 0;
            }
            return 0;
        }
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

    protected Component generateEnumField(Field field, Binder binder) {

        Class clazzEnum = field.getType();
        ComboBox comboBox = new ComboBox<>();

        comboBox.setEmptySelectionCaption(EMPTY_ENUM_TEXT);

        comboBox.setItems(field.getType().getEnumConstants());

        binder.bind(comboBox, field.getName());

        return comboBox;
    }

    protected Component generateStringField(Field field, Binder binder) {

        Component textField = null;

        if (field.isAnnotationPresent(BigString.class)) {
            textField = new TextArea();
        } else textField = new TextField();

        binder.bind((HasValue) textField, field.getName());

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
