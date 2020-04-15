package org.sr3u.photoframe.client.ui.settings;

import javax.swing.*;

public class CheckboxSettingPanel extends SettingPanel<Boolean> {
    JCheckBox checkBox = new JCheckBox();

    public CheckboxSettingPanel(String name, String value) {
        super(name);
        checkBox.setSelected(Boolean.parseBoolean(value));
        add(checkBox);
    }


    @Override
    public Boolean getValue() {
        return checkBox.isSelected();
    }
}
