package org.sr3u.photoframe.client.ui.settings.filters;

import javax.swing.*;
import java.awt.*;

public class UpDownButtonsPanel extends JPanel {
    protected final JButton down = new JButton("↓");
    protected final JButton up = new JButton("↑");
    private PanelDelegate delegate;

    public UpDownButtonsPanel() {
        this.add(up);
        this.add(down);
        up.addActionListener(e -> getDelegate().up(UpDownButtonsPanel.this));
        down.addActionListener(e -> getDelegate().down(UpDownButtonsPanel.this));
        this.setLayout(new GridBagLayout());
    }

    public PanelDelegate getDelegate() {
        if (delegate != null) {
            return delegate;
        }
        return new PanelDelegate();
    }

    public void setDelegate(PanelDelegate delegate) {
        this.delegate = delegate;
    }
}
