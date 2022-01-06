package org.sr3u.retroframe.client.ui.settings;

import javax.swing.*;
import java.awt.*;

public abstract class SettingPanel<T> extends JPanel {
    private final JLabel name;

    public SettingPanel(String name) {
        this.name = new JLabel();
        this.name.setText(name);
        this.setLayout(new GridLayout(1, 2, 10, 0));
        this.add(this.name);
    }

    @Override
    public String getName() {
        return name.getText();
    }

    public abstract T getValue();
}
