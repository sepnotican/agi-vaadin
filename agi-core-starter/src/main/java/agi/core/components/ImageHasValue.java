package agi.core.components;

import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageHasValue extends Image implements HasValue<byte[]> {

    private List<ValueChangeListener<byte[]>> listeners = new ArrayList<>(1);

    @Override
    public void setValue(byte[] value) {
        byte[] oldValue = (byte[]) this.getData();
        this.setData(value);
        this.markAsDirty();
        listeners.forEach( l -> l.valueChange(new ValueChangeEvent<byte[]>(this, this, oldValue, true)));
    }

    @Override
    public byte[] getValue() {
        return (byte[]) this.getData();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<byte[]> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

}
