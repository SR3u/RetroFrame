package org.sr3u.photoframe.client.ui.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.client.ui.main.ImageWindow;
import org.sr3u.photoframe.server.Main;
import org.sr3u.photoframe.settings.Settings;
import sr3u.streamz.streams.Streamex;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsWindow {
    private static final Logger log = LogManager.getLogger(SettingsWindow.class);

    public static final Dimension MINIMUM_SIZE = new Dimension(200, 200);
    private final Settings currentSettings;
    private final Properties settings;
    private final Map<String, Class<?>> settingsClasses;

    private List<SettingPanel<?>> settingPanels;

    private final Map<Class<?>, Class<? extends SettingPanel<?>>> settingPanelMap = Stream.of(
            new Pair<>(Boolean.class, CheckboxSettingPanel.class),
            new Pair<>(boolean.class, CheckboxSettingPanel.class),
            new Pair<>(Integer.class, DigitsFieldSettingPanel.class),
            new Pair<>(int.class, DigitsFieldSettingPanel.class),
            new Pair<>(Long.class, DigitsFieldSettingPanel.class),
            new Pair<>(long.class, DigitsFieldSettingPanel.class),
            new Pair<>(String.class, StringSettingPanel.class)
    ).collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a, b) -> b));

    JFrame frame;
    JScrollPane scrollPane;
    JPanel scrollPaneContent;

    public SettingsWindow(Settings currentSettings) {
        frame = new JFrame();
        frame.setTitle(ImageWindow.TITLE_NAME + " Settings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(MINIMUM_SIZE);
        scrollPane = new JScrollPane();
        scrollPane.setMinimumSize(MINIMUM_SIZE);
        scrollPaneContent = new JPanel();
        scrollPaneContent.setMinimumSize(MINIMUM_SIZE);
        scrollPaneContent.setLayout(new BoxLayout(scrollPaneContent, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(scrollPaneContent);
        this.currentSettings = currentSettings;
        this.settings = currentSettings.getProperties();
        settingsClasses = currentSettings.getPropertiesClasses();
        fillSettings();
        frame.add(scrollPane);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                String[] buttons = {"Save", "Save and restart", "Don't save"};

                int rc = JOptionPane.showOptionDialog(null, "Save settings?", "Save settings?",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[2]);
                switch (rc) {
                    case 0:
                        Settings.load(getProperties()).save(Main.SETTINGS_PROPERTIES);
                        break;
                    case 1:
                        Settings.load(getProperties()).save(Main.SETTINGS_PROPERTIES);
                        try {
                            Main.restartApplication();
                        } catch (IOException | URISyntaxException e) {
                            log.error(e);
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                    default:
                        break;
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        settingPanels.forEach(sp -> properties.setProperty(sp.getName(), String.valueOf(sp.getValue())));
        return properties;
    }

    private void fillSettings() {
        settingPanels = Streamex.ofStream(settings.keySet().stream())
                .mapToString()
                .map(k -> new Pair<>(k, settings.getProperty(k)))
                .map(p -> {
                    Class<?> clazz = settingsClasses.get(p.getKey());
                    Class<? extends SettingPanel<?>> panelClass = settingPanelMap.getOrDefault(clazz, StringSettingPanel.class);
                    return panelClass.getConstructor(String.class, String.class).newInstance(p.getKey(), p.getValue());
                }).collect(Collectors.toList());
        settingPanels.forEach(sp -> {
            sp.setMinimumSize(new Dimension(32, 100));
            scrollPaneContent.add(sp);
        });

    }
}