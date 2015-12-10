package client.ui;

import javax.swing.*;

public class ClientListView {
    final JComponent component;
    final JTable table;

    public ClientListView(JComponent component, JTable table) {
        this.component = component;
        this.table = table;
    }

    public JComponent getComponent() {
        return component;
    }

    public JTable getTable() {
        return table;
    }
}
