package org.sr3u.photoframe.client.ui.settings.filters;


import lombok.Setter;
import org.sr3u.photoframe.client.filters.FilterDescriptor;
import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.client.filters.ImageFilters;
import org.sr3u.photoframe.client.filters.utils.Palette;
import sr3u.streamz.streams.Streamex;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilterPanel extends UpDownButtonsPanel {
    protected JComboBox<String> name;
    protected JTextField parameters;
    protected JComboBox<String> paletteParameter;


    protected final JButton delete = new JButton("x");

    @Setter
    private FiltersWindow window;


    public FilterPanel(FilterDescriptor filterDescriptor) {
        this(filterDescriptor.getName(), filterDescriptor.getParameters());
    }

    public FilterPanel(String name, List<String> parameters) {
        this(name, Streamex.ofStream(parameters.stream()).mapToString().joined(" "));
    }

    public FilterPanel(String name, String parameters) {
        init(name, parameters);
    }

    public void init(String name, String parameters) {
        super.init();
        this.removeAll();
        List<String> allFilters = ImageFilters.getAllAvailable();
        this.name = new JComboBox<>(allFilters.toArray(new String[0]));
        this.name.setSelectedItem(name);
        this.name.setEditable(true);
        this.name.getEditor().addActionListener(e -> refresh());
        this.name.addActionListener(e -> refresh());
        this.add(this.name);
        ImageFilter.Info info = ImageFilters.aboutFilter(name);
        Component parametersComponent = null;
        if (info.isPaletteArgument()) {
            this.parameters = null;
            List<String> strings = Streamex.ofStream(Palette.allNames().stream()).collect(Collectors.toList());
            this.paletteParameter = new JComboBox<>(strings.toArray(new String[0]));
            this.paletteParameter.setEditable(true);
            if (parameters.trim().split(" ").length == 1) {
                paletteParameter.setSelectedItem(parameters.trim());
            } else {
                paletteParameter.setSelectedItem(parameters);
            }
            parametersComponent = this.paletteParameter;
        } else {
            this.paletteParameter = null;
            this.parameters = new JTextField();
            this.parameters.setText(parameters);
            parametersComponent = this.parameters;
        }
        parametersComponent.setMinimumSize(new Dimension(320, 24));
        parametersComponent.setPreferredSize(new Dimension(320, 24));
        this.add(parametersComponent);
        this.delete.addActionListener(e -> getDelegate().delete(FilterPanel.this));
        this.add(this.delete);
    }

    private void refresh() {
        try {
            FilterDescriptor filterDescriptor = this.toFilterDescriptor();
            this.init(filterDescriptor.getName(),
                    String.join(" ", filterDescriptor.getParameters()));
            Optional.ofNullable(window).ifPresent(FiltersWindow::forceRedraw);
        } catch (Exception ignored) {
        }
    }

    public FilterDescriptor toFilterDescriptor() {
        String name = (String) this.name.getSelectedItem();
        if (!ImageFilters.getAllAvailable().contains(name)) {
            throw new RuntimeException("Invalid Filter " + name);
        }
        String parameters;
        if (this.parameters != null) {
            parameters = this.parameters.getText().trim();
        } else {
            String selectedItem = (String) this.paletteParameter.getSelectedItem();
            if (!Palette.isValid(selectedItem)) {
                throw new RuntimeException("Invalid Palette " + selectedItem);
            }
            parameters = String.valueOf(selectedItem);
        }
        FilterDescriptor filterDescriptor = new FilterDescriptor(name, Streamex.of(parameters.split(" "))
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()));
        if (!ImageFilters.isValid(filterDescriptor)) {
            throw new RuntimeException("Invalid Filter " + filterDescriptor);
        }
        return filterDescriptor;
    }
}
