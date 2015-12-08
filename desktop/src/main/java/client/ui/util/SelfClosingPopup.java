package client.ui.util;

import javax.swing.*;
import java.awt.*;

import static client.ui.util.GuiHelper.*;

public class SelfClosingPopup {

    public static void showJPopup(Component owner, JComponent content, int x, int y) {
        JPopupMenu popup = new JPopupMenu();

        JComponent wrappedContent = borderLayout()
                .center(content)
                .south(
                        toolbar(FlowLayout.TRAILING, button("Close", () -> popup.setVisible(false)))
                )
                .build();

        popup.setLayout(new BorderLayout());
        popup.add(wrappedContent);
        popup.show(owner, x, y);
    }


    public static void showPopup(Component owner, JComponent content, int x, int y) {
        Forwarder fwd = new Forwarder();
        JComponent wrappedContent = withBorder(borderLayout()
                .center(content)
                .south(
                        toolbar(FlowLayout.TRAILING, button("Close", fwd))
                )
                .build(), BorderFactory.createLineBorder(Color.gray));
        Popup popup = PopupFactory.getSharedInstance().getPopup(owner, wrappedContent, x, y);
        fwd.target = popup::hide;
        popup.show();
    }

    static class Forwarder implements Runnable {

        Runnable target;

        @Override
        public void run() {
            target.run();
        }
    }

}
