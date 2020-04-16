package org.sr3u.photoframe.client.ui.settings.filters;


import org.sr3u.photoframe.client.filters.FilterDescriptor;
import org.sr3u.photoframe.client.filters.ImageFilters;
import sr3u.streamz.streams.Streamex;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterPanel extends UpDownButtonsPanel {
    protected final JComboBox<String> name;
    protected final JTextField parameters;


    protected final JButton delete = new JButton("x");


    public FilterPanel(FilterDescriptor filterDescriptor) {
        this(filterDescriptor.getName(), filterDescriptor.getParameters());
    }

    public FilterPanel(String name, List<String> parameters) {
        this(name, Streamex.ofStream(parameters.stream()).mapToString().joined(" "));
    }

    public FilterPanel(String name, String parameters) {
        List<String> allFilters = ImageFilters.getAllAvailable();
        this.name = new JComboBox<>(allFilters.toArray(new String[0]));
        this.name.setSelectedItem(name);
        this.add(this.name);
        this.parameters = new JTextField();
        this.parameters.setText(parameters);
        this.parameters.setMinimumSize(new Dimension(320, 24));
        this.parameters.setPreferredSize(new Dimension(320, 24));
        this.add(this.parameters);
        delete.addActionListener(e -> getDelegate().delete(FilterPanel.this));
        this.add(delete);
    }

    public FilterDescriptor toFilterDescriptor() {
        String name = (String) this.name.getSelectedItem();
        String parameters = this.parameters.getText().trim();
        return new FilterDescriptor(name, Streamex.of(parameters.split(" "))
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()));
    }
}
