package org.sr3u.retroframe.client.ui.settings;

public class StringSettingPanel extends TextFieldSettingPanel<String> {
    public StringSettingPanel(String name, String value) {
        super(name, value);
    }

    @Override
    public String getValue() {
        return getValueText();
    }
}
