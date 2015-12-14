package client.ui.util;

import client.ui.RentalClasses;
import common.domain.RentalClass;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.core.convert.converter.Converter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.function.Consumer;

public class GuiHelper {

    public static JDatePickerImpl datePicker(UtilDateModel dateModel) {
        return new JDatePickerImpl(new JDatePanelImpl(dateModel));
    }

    public static JTextField textField(int size, Consumer<String> changeListener) {
        JTextField tf = new JTextField(size);
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

    public static JComboBox<RentalClass> rentalClassChooser(RentalClasses rentalClasses) {
        JComboBox<RentalClass> combo = new JComboBox<>(rentalClasses.getComboBoxModel());
        combo.setRenderer(convertingListCellRenderer(rc -> rc != null ? rc.getName() : "<all>"));
        return combo;
    }


}
