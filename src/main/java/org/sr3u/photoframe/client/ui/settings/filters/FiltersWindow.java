package org.sr3u.photoframe.client.ui.settings.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.client.filters.FilterDescriptor;
import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.client.filters.ImageFilters;
import org.sr3u.photoframe.client.ui.main.ImageWindow;
import org.sr3u.photoframe.client.ui.settings.ScrollableWindow;
import org.sr3u.photoframe.server.Main;
import sr3u.streamz.streams.Streamex;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FiltersWindow extends ScrollableWindow {

    private static final Logger log = LogManager.getLogger(FiltersWindow.class);

    private final List<UpDownButtonsPanel> allPanels;
    private final TechnicalFilterPanel technicalFilterPanel;
    private final ImageWindow imageWindow;

    private PanelDelegate panelDelegate = new PanelDelegate() {
        @Override
        public void add(TechnicalFilterPanel panel) {
            scrollPaneContent.remove(technicalFilterPanel);
            FilterPanel identity = new FilterPanel("identity", "");
            addPanel(identity);
            identity.setIndex(panel.getIndex());
            identity.setDelegate(this);
            scrollPaneContent.add(technicalFilterPanel);
            up(identity);
        }

        @Override
        public void delete(FilterPanel panel) {
            removePanel(panel);
            panel.setDelegate(null);
            updateIndices(null);
        }

        @Override
        public void down(UpDownButtonsPanel panel) {
            updateIndices(panel);
        }

        @Override
        public void up(UpDownButtonsPanel panel) {
            updateIndices(panel);
        }

        private void updateIndices(UpDownButtonsPanel panel) {
            allPanels.forEach(scrollPaneContent::remove);
            List<UpDownButtonsPanel> tmp = new ArrayList<>(allPanels.size());
            allPanels.stream().filter(p -> p != panel).forEach(tmp::add);
            if (panel != null) {
                if (panel.getIndex() < 0) {
                    panel.setIndex(0);
                }
                if (panel.getIndex() >= allPanels.size()) {
                    panel.setIndex(allPanels.size() - 1);
                }
                tmp.add(panel.getIndex(), panel);
            }
            allPanels.clear();
            for (int i = 0; i < tmp.size(); i++) {
                UpDownButtonsPanel current = tmp.get(i);
                scrollPaneContent.add(current);
                current.setIndex(i);
                allPanels.add(current);
            }
            forceRepaint();
        }

        @Override
        public void apply() {
            try {
                String newFilterChain = Streamex.ofStream(allPanels.stream())
                        .filter(p -> p instanceof FilterPanel)
                        .map(p -> (FilterPanel) p)
                        .map(FilterPanel::toFilterDescriptor)
                        .mapToString(FilterDescriptor::toString)
                        .joined(" | ");
                if (newFilterChain == null || newFilterChain.trim().isEmpty()) {
                    newFilterChain = "identity";
                }
                try {
                    ImageFilter parsed = ImageFilters.parse(newFilterChain);
                    imageWindow.setImageFilter(parsed);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Failed to set filters:\n" + newFilterChain,
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Main.settings.getClient().setImageFitlerChain(newFilterChain);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Failed to set filters:\n" + e.getMessage(),
                        "ERROR", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        }

        @Override
        public void applyAndSave() {
            try {
                apply();
                Main.settings.save(Main.SETTINGS_PROPERTIES);
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
            }
        }
    };

    private void forceRepaint() {
        frame.invalidate();
        frame.revalidate();
        frame.repaint();
    }

    public FiltersWindow(ImageWindow imageWindow) {
        super("Filters");
        this.imageWindow = imageWindow;
        String imageFitlerChain = Main.settings.getClient().getImageFitlerChain();
        List<FilterDescriptor> filterDescriptors = ImageFilters.parseDescriptors(imageFitlerChain);
        allPanels = filterDescriptors.stream()
                .map(FilterPanel::new)
                .peek(fp -> fp.setMinimumSize(new Dimension(32, 32)))
                .peek(scrollPaneContent::add)
                .peek(fp -> fp.setDelegate(panelDelegate))
                .peek(fp -> fp.setWindow(this))
                .collect(Collectors.toList());
        technicalFilterPanel = new TechnicalFilterPanel();
        technicalFilterPanel.setDelegate(panelDelegate);
        addPanel(technicalFilterPanel);
        technicalFilterPanel.setIndex(allPanels.size() - 1);
        panelDelegate.down(technicalFilterPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void addPanel(UpDownButtonsPanel panel) {
        scrollPaneContent.add(panel);
        allPanels.add(panel);
    }

    private void removePanel(UpDownButtonsPanel panel) {
        scrollPaneContent.remove(panel);
        allPanels.remove(panel);
    }

    public void forceRedraw() {
        frame.invalidate();
        frame.revalidate();
        frame.repaint();
    }
}
