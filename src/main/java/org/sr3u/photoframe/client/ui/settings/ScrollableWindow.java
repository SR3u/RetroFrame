package org.sr3u.photoframe.client.ui.settings;

import org.sr3u.photoframe.client.ui.ClientWindow;

import javax.swing.*;
import java.awt.*;

public abstract class ScrollableWindow extends ClientWindow {

    public static final Dimension MINIMUM_SIZE = new Dimension(200, 200);

    protected final JScrollPane scrollPane;
    protected final JPanel scrollPaneContent;

    public ScrollableWindow(String title) {
        super();
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, (int) MINIMUM_SIZE.getWidth()));
        scrollPane = new JScrollPane();
        scrollPane.setMinimumSize(MINIMUM_SIZE);
        scrollPaneContent = new JPanel();
        scrollPaneContent.setMinimumSize(MINIMUM_SIZE);
        scrollPaneContent.setLayout(new BoxLayout(scrollPaneContent, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(scrollPaneContent);
        frame.add(scrollPane);
    }
}
