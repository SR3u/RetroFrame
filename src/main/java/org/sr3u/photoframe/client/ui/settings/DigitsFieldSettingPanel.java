package org.sr3u.photoframe.client.ui.settings;

import java.text.DecimalFormat;
import java.text.Format;

public class DigitsFieldSettingPanel extends FilteredTextFieldSettingPanel {
    public DigitsFieldSettingPanel(String name, String value) {
        super(name, value);
    }

    @Override
    protected Format createFormatter() {
        DecimalFormat dec = new DecimalFormat();
        dec.setGroupingUsed(false);
        return dec;
    }

}
