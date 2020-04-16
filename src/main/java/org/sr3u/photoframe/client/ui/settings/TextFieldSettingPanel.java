package org.sr3u.photoframe.client.ui.settings;

import javax.swing.*;
import java.awt.*;

public abstract class TextFieldSettingPanel<T> extends SettingPanel<T> {
    protected final JTextField value;

    public TextFieldSettingPanel(String name, String value) {
        super(name);
        this.value = new JTextField();
        this.value.setText(value);
        this.value.setMinimumSize(new Dimension(32, 32));
        this.add(this.value);
    }

    public String getValueText() {
        return value.getText();
    }
}