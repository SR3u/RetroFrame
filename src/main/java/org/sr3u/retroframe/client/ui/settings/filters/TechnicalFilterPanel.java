package org.sr3u.retroframe.client.ui.settings.filters;

import javax.swing.*;

public class TechnicalFilterPanel extends UpDownButtonsPanel {

    public TechnicalFilterPanel() {
        super();
        JButton add = new JButton("+");
        add(add);
        add.addActionListener(e -> getDelegate().add(TechnicalFilterPanel.this));
        JButton apply = new JButton("Apply");
        add(apply);
        apply.addActionListener(e -> getDelegate().apply());
        JButton applyAndSave = new JButton("Apply and save");
        add(applyAndSave);
        applyAndSave.addActionListener(e -> getDelegate().applyAndSave());
    }
}
