package org.sr3u.retroframe.client.ui.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.text.Format;
import java.text.ParseException;

public abstract class FilteredTextFieldSettingPanel extends SettingPanel<String> {
    private final JFormattedTextField value;
    private static final Logger log = LogManager.getLogger(FilteredTextFieldSettingPanel.class);

    public FilteredTextFieldSettingPanel(String name, String value) {
        super(name);
        Format formatter = createFormatter();
        this.value = new JFormattedTextField(formatter);
        try {
            this.value.setValue(formatter.parseObject(value));
        } catch (ParseException e) {
            log.error(e);
            e.printStackTrace();
        }
        add(this.value);
    }

    protected abstract java.text.Format createFormatter();

    @Override
    public String getValue() {
        return value.getText();
    }
}
