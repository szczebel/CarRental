package client.ui;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.core.convert.converter.Converter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class GuiHelper {

    public static JButton button(String label, Runnable action) {
        return new JButton(new AbstractAction(label) {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
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
        JPanel panel = new JPanel();
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


    public static JComponent inScrollPane(JComponent scrollable) {
        return new JScrollPane(scrollable);
    }

    static TableCellRenderer convertingRenderer(Converter<Object, Object> converter) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, converter.convert(value), isSelected, hasFocus, row, column);
            }
        };
    }

    public static BorderLayoutBuilder borderLayout() {
        return new BorderLayoutBuilder();
    }

    public static TabsBuilder tabbedPane(int tabPlacement) {
        return new TabsBuilder(tabPlacement);
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
