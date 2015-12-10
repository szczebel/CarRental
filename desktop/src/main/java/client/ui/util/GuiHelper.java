package client.ui.util;

import client.ui.RentalClasses;
import com.jgoodies.forms.builder.FormBuilder;
import common.domain.RentalClass;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.core.convert.converter.Converter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class GuiHelper {

    public static JButton button(String label, Runnable action) {
        return button(label, e -> action.run());
    }

    public static JButton button(String label, Consumer<ActionEvent> action) {
        return new JButton(new AbstractAction(label) {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.accept(e);
            }
        });
    }

    public static JLabel label(String text) {
        return new JLabel(text);
    }

    public static JDatePickerImpl datePicker(UtilDateModel dateModel) {
        return new JDatePickerImpl(new JDatePanelImpl(dateModel));
    }

    public static JComponent toolbar(JComponent... components) {
        return toolbar(FlowLayout.LEADING, components);
    }

    public static JComponent toolbar(int align, JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(align));
        Arrays.asList(components).forEach(panel::add);
        return panel;
    }

    public static JComponent withTitledBorder(JComponent component, String title) {
        return withBorder(component, BorderFactory.createTitledBorder(title));
    }

    public static JComponent withBorder(JComponent component, Border border) {
        component.setBorder(border);
        return component;
    }

    public static JTextField textField(int size, Consumer<String> changeListener) {
        JTextField tf = new JTextField(20);
        tf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            void onChange() {
                changeListener.accept(tf.getText().trim());
            }
        });
        return tf;
    }


    public static JComponent inScrollPane(JComponent scrollable) {
        return new JScrollPane(scrollable);
    }

    public static TableCellRenderer convertingRenderer(Converter<Object, Object> converter) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, converter.convert(value), isSelected, hasFocus, row, column);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> ListCellRenderer<T> convertingListCellRenderer(Converter<T, String> converter) {
        return (ListCellRenderer<T>) new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, converter.convert((T)value), index, isSelected, cellHasFocus);
            }
        };
    }



    public static BorderLayoutBuilder borderLayout() {
        return new BorderLayoutBuilder();
    }

    public static TabsBuilder tabbedPane(int tabPlacement) {
        return new TabsBuilder(tabPlacement);
    }

    public static JComponent grid(int rows, int columns, JComponent... components) {
        JPanel panel = new JPanel(new GridLayout(rows, columns));
        Arrays.asList(components).forEach(panel::add);
        return panel;
    }

    public static JComboBox<RentalClass> rentalClassChooser(RentalClasses rentalClasses) {
        JComboBox<RentalClass> combo = new JComboBox<>(rentalClasses.getComboBoxModel());
        combo.setRenderer(convertingListCellRenderer(rc -> rc != null ? rc.getName() : "<all>"));
        return combo;
    }

    public static SimpleFormBuilder simpleForm() {
        return new SimpleFormBuilder();
    }

    public static class SimpleFormBuilder {
        java.util.List<String> labels = new ArrayList<>();
        java.util.List<JComponent> components = new ArrayList<>();

        public SimpleFormBuilder addRow(String label, JComponent component) {
            labels.add(label);
            components.add(component);
            return this;
        }

        public JComponent build() {
            StringBuilder rowSpec = new StringBuilder("");
            labels.forEach(s-> rowSpec.append("p, $lg,"));
            FormBuilder fb = FormBuilder.create()
                    .columns("pref:grow, ${label-component-gap}, [100dlu, pref]")
                    .rows(rowSpec.toString());
            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                JComponent c = components.get(i);
                fb.add(label).xy(1, 1 + i*2).add(c).xy(3, 1 + i*2);
            }
            return fb.build();
        }
    }

    public static class TabsBuilder {
        private final JTabbedPane tabs;

        public TabsBuilder(int tabPlacement) {
            tabs = new JTabbedPane(tabPlacement);
        }

        public TabsBuilder addTab(String title, JComponent tab) {
            tabs.addTab(title, tab);
            return this;
        }

        public JComponent build() {
            return tabs;
        }
    }

    @SuppressWarnings("unused")
    public static class BorderLayoutBuilder {
        private final JPanel panel = new JPanel(new BorderLayout());

        public BorderLayoutBuilder north(JComponent component) {
            panel.add(component, BorderLayout.NORTH);
            return this;
        }

        public BorderLayoutBuilder south(JComponent component) {
            panel.add(component, BorderLayout.SOUTH);
            return this;
        }

        public BorderLayoutBuilder center(JComponent component) {
            panel.add(component, BorderLayout.CENTER);
            return this;
        }

        public BorderLayoutBuilder west(JComponent component) {
            panel.add(component, BorderLayout.WEST);
            return this;
        }

        public BorderLayoutBuilder east(JComponent component) {
            panel.add(component, BorderLayout.EAST);
            return this;
        }

        public JComponent build() {
            return panel;
        }
    }
}
