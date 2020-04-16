package org.sr3u.photoframe.client.ui.settings.filters;

import javax.swing.*;

public class TechnicalFilterPanel extends UpDownButtonsPanel {
    private JButton add = new JButton("+");

    private JButton apply = new JButton("Apply");

    private JButton applyAndSave = new JButton("Apply and save");

    public TechnicalFilterPanel() {
        super();
        add(add);
        add.addActionListener(e -> getDelegate().add(TechnicalFilterPanel.this));
        add(apply);
        apply.addActionListener(e -> getDelegate().apply());
        add(applyAndSave);
        applyAndSave.addActionListener(e -> getDelegate().applyAndSave());
    }
}
