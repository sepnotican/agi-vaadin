package com.sepnotican.springjpaformautocreator.generator.form.generic;

import com.sepnotican.springjpaformautocreator.generator.annotations.*;
import com.sepnotican.springjpaformautocreator.generator.form.IFormHandler;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AbstractElementForm<T> extends VerticalLayout {

    public static final String BTN_SAVE_TEXT = "Save";
    public static final String BTN_RELOAD_TEXT = "Reload";
    public static final String EMPTY_ENUM_TEXT = "<empty>";
    private final static Logger logger = Logger.getLogger(AbstractElementForm.class);
    protected T entity;
    protected Binder<T> binder;
    protected Layout defaultControlPanel;
    protected IFormHandler formHandler;
    protected String formCachedName;

    private ApplicationContext context;
    private JpaRepository<T, Object> repository;

    public AbstractElementForm(JpaRepository<T, Object> repository, IFormHandler formHandler, ApplicationContext context) {
        this.repository = repository;
        this.formHandler = formHandler;
        this.context = context;
    }

    public void init(T entity, boolean isNewInstance, String formCachedName) {
        this.formCachedName = formCachedName;
        removeAllComponents();

        this.entity = entity;
        binder = new Binder(entity.getClass());

        initDefaultControlPanel(binder);

        Class clazz = entity.getClass();
        Field[] fieldsArray = clazz.getDeclaredFields();
        ArrayList<Field> fieldArrayList = createOrderedElementsList(fieldsArray);

        for (Field field : fieldArrayList) {

            Component component = getComponentByFieldAndBind(field, binder);

            if (component == null) continue;

            if (field.isAnnotationPresent(javax.persistence.Id.class))
                ((HasValue) component).setReadOnly(true);

            makeUpCaptionForField(field, component);

            component.setSizeUndefined();
            addComponent(component);

        }
        binder.bindInstanceFields(entity);

        if (!isNewInstance) binder.readBean(entity);

        setSizeFull();
        setWidth("50%");

    }

    protected ArrayList<Field> createOrderedElementsList(Field[] fieldsArray) {
        return new ArrayList<>(Arrays.stream(fieldsArray)
                .sorted(new UIOrderComparator())
                .collect(Collectors.toList()));
    }

    protected void initDefaultControlPanel(Binder<T> binder) {
        defaultControlPanel = new HorizontalLayout();

        MenuBar menuBar = new MenuBar();
        MenuBar.MenuItem menuItemSave = menuBar.addItem(BTN_SAVE_TEXT,
                VaadinIcons.CHECK,
                event -> {
                    try {
                        binder.writeBean(entity);
                        repository.save(entity);
                        binder.readBean(entity); //reload autogenerated fields
                        formHandler.refreshElementCaption(entity, formCachedName);
                    } catch (Exception e) {
                        Notification.show("Error", "Error while saving element", Notification.Type.ERROR_MESSAGE);
                        logger.error("Error while saving element: " + entity.getClass().getCanonicalName());
                    }
                });

        MenuBar.MenuItem menuItemReload = menuBar.addItem(BTN_RELOAD_TEXT,
                VaadinIcons.REFRESH,
                event -> binder.readBean(entity));

        defaultControlPanel.addComponent(menuBar);
        addComponent(defaultControlPanel);
    }

    public String getFormCachedName() {
        return formCachedName;
    }

    protected void makeUpCaptionForField(Field field, Component component) {
        if (field.isAnnotationPresent(Synonym.class)) {
            component.setCaption(field.getAnnotation(Synonym.class).value());
        } else component.setCaption(field.getName());
    }

    protected Component getComponentByFieldAndBind(Field field, Binder binder) {
        if (field.getType().equals(Long.class)
                || field.getType().equals(long.class)) {
            return generateLongField(field, binder);

        } else if (field.getType().equals(Double.class)
                || field.getType().equals(double.class)) {
            return generateDoubleFieild(field, binder);

        } else if (field.getType().equals(Float.class)
                || field.getType().equals(float.class)) {
            return generateFloatFieild(field, binder);

        } else if (field.getType().equals(String.class)) {
            return generateStringField(field, binder);

        } else if (field.getType().isEnum()) {
            return generateEnumField(field, binder);

        } else return generateLinkedObjectField(field, binder);
    }

    protected Component generateFloatFieild(Field field, Binder binder) {
        TextField textField = new TextField(field.getName());
        binder.forField(textField)
                .withConverter(new StringToFloatConverter("Must be a float value"))
                .bind(field.getName());
        return textField;
    }

    protected Component generateDoubleFieild(Field field, Binder binder) {
        TextField textField = new TextField(field.getName());
        binder.forField(textField)
                .withConverter(new StringToDoubleConverter("Must be a double value"))
                .bind(field.getName());
        return textField;
    }

    protected Component generateEnumField(Field field, Binder binder) {
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
        return textField;
    }

    protected Component generateLinkedObjectField(Field field, Binder binder) {

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

    protected class UIOrderComparator implements Comparator<Field> {

        @Override
        public int compare(Field o1, Field o2) {
            if (!o1.isAnnotationPresent(AgiDrawOrder.class)
                    && o2.isAnnotationPresent(AgiDrawOrder.class))
                return 1;
            else if (o1.isAnnotationPresent(AgiDrawOrder.class)
                    && !o2.isAnnotationPresent(AgiDrawOrder.class))
                return -1;
            else if (!o1.isAnnotationPresent(AgiDrawOrder.class)
                    && !o2.isAnnotationPresent(AgiDrawOrder.class))
                return 0;
            else if (o1.getAnnotation(AgiDrawOrder.class).drawOrder() >
                    o2.getAnnotation(AgiDrawOrder.class).drawOrder())
                return 1;
            else if ((o1.getAnnotation(AgiDrawOrder.class).drawOrder() <
                    o2.getAnnotation(AgiDrawOrder.class).drawOrder()))
                return -1;

            return 0;
        }
    }
}
