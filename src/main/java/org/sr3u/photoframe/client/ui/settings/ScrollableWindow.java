package org.sr3u.photoframe.client.ui.settings;

import javax.swing.*;
import java.awt.*;

public abstract class ScrollableWindow {

    public static final Dimension MINIMUM_SIZE = new Dimension(200, 200);

    protected final JFrame frame;
    protected final JScrollPane scrollPane;
    protected final JPanel scrollPaneContent;

    public ScrollableWindow(String title) {
        frame = new JFrame();
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
