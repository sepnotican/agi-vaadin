package com.sepnotican.agi.core.form.generic;

import com.google.common.collect.ImmutableSet;
import com.sepnotican.agi.core.annotations.AgiEntity;
import com.sepnotican.agi.core.annotations.BigString;
import com.sepnotican.agi.core.annotations.LinkedObject;
import com.sepnotican.agi.core.annotations.Picture;
import com.sepnotican.agi.core.annotations.RepresentationResolver;
import com.sepnotican.agi.core.annotations.Synonym;
import com.sepnotican.agi.core.dao.CompareType;
import com.sepnotican.agi.core.dao.CriteriaFilter;
import com.sepnotican.agi.core.dao.GenericBackendDataProvider;
import com.sepnotican.agi.core.dao.GenericDaoFactory;
import com.sepnotican.agi.core.form.util.VaadinProvidersFactory;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.server.SerializableFunction;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
@Slf4j
public class GenericFieldGenerator {
    @Autowired
    GenericDaoFactory genericDaoFactory;
    @Value("${agi.forms.element.enum-null-selection}")
    private String EMPTY_ENUM_TEXT;

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
        } else if (field.getType().equals(byte[].class) && field.isAnnotationPresent(Picture.class)) {
            component = generatePictureComponent(field);
            makeUpCaptionForField(field, ((HorizontalLayout) component).getComponent(1));
            return component;
        } else {
            log.error("getComponentByField(): not implemented cast for {}", field.getType().getCanonicalName());
            return null;
        }
        makeUpCaptionForField(field, component);
        return component;
    }

    protected com.vaadin.ui.Component generatePictureComponent(Field field) {

        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Upload.Receiver receiver = (filename, mimeType) -> byteArrayOutputStream;
        Upload upload = new Upload("Select the image", receiver);
        upload.addSucceededListener(succeededEvent -> {
            image.setData(byteArrayOutputStream.toByteArray());
            image.markAsDirtyRecursive();
        });
        if (field.getAnnotation(Picture.class).editable()) {
            image.addClickListener(click -> {
                upload.submitUpload();
            });
        }
        return new HorizontalLayout(image, upload);
    }

    @SuppressWarnings("unchecked")
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
        } else if (field.getType().equals(byte[].class) && field.isAnnotationPresent(Picture.class)) {
            Image image = Image.class.cast(HorizontalLayout.class.cast(component).getComponent(0));
            Upload upload = Upload.class.cast(HorizontalLayout.class.cast(component).getComponent(1));
            upload.addSucceededListener(new Upload.SucceededListener() {
                @Override
                @SneakyThrows
                public void uploadSucceeded(Upload.SucceededEvent event) {
                    if (!field.isAccessible()) field.setAccessible(true);
                    field.set(binder.getBean(), image.getData());
                    image.markAsDirty();
                }
            });
            //binder.bind((HasValue) component, field.getName());
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
        com.vaadin.ui.Component textField;
        if (field.isAnnotationPresent(BigString.class)) {
            textField = new TextArea();
        } else textField = new TextField();
        return textField;
    }

    protected com.vaadin.ui.Component generateLongField(Field field) {
        return new TextField(field.getName());
    }

    @SuppressWarnings("unchecked")
    protected com.vaadin.ui.Component generateLinkedObjectField(Field field) {
        final Class<?> fieldType = field.getType();
        if (!fieldType.isAnnotationPresent(AgiEntity.class)) {
            log.error("Attempt to create field without AgiEntity annotation. Classname = {} Field name = {}",
                    field.getClass().getCanonicalName(), field.getName());
            return null;
        }
        ComboBox comboBox = new ComboBox<>();
        comboBox.setEmptySelectionCaption(EMPTY_ENUM_TEXT);
        String methodName;
        if (field.getType().isAnnotationPresent(RepresentationResolver.class)) {
            methodName = field.getType().getAnnotation(RepresentationResolver.class).value();
        } else {
            methodName = "toString";
        }
        comboBox.setItemCaptionGenerator(VaadinProvidersFactory.getItemCaptionGenerator(field, methodName));
        comboBox.setDataProvider(new GenericBackendDataProvider(fieldType,
                genericDaoFactory.getGenericDaoForClass(fieldType)).withConvertedFilter(new SerializableFunction() {
            @Override
            @SneakyThrows
            public Object apply(Object o) {
                if (o instanceof String) {
                    String synonymFieldName = fieldType.getAnnotation(AgiEntity.class).fieldForInputSearch();
                    Class<?> synonymFieldType = fieldType.getDeclaredField(synonymFieldName).getType();
                    CompareType compareType = synonymFieldType == String.class ? CompareType.LIKE : CompareType.EQUALS;
                    return ImmutableSet.of(new CriteriaFilter(fieldType, synonymFieldName, o, compareType));
                } else return o;
            }
        }));
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

    protected void makeUpCaptionForMethodProvidedColumn(Method method, Grid.Column column) {
        if (method.isAnnotationPresent(Synonym.class)) {
            column.setCaption(method.getAnnotation(Synonym.class).value());
        } else column.setCaption(method.getName());
    }

    protected void makeUpCaptionForMethodProvidedComponent(Method method, com.vaadin.ui.Component component) {
        if (method.isAnnotationPresent(Synonym.class)) {
            component.setCaption(method.getAnnotation(Synonym.class).value());
        } else component.setCaption(method.getName());
    }

}
