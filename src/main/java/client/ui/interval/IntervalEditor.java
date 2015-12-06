package client.ui.interval;

import common.util.Interval;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.*;
import static common.util.TimeUtils.toMidnight;

public class IntervalEditor implements Supplier<Interval> {

    UtilDateModel from;
    UtilDateModel to;
    JComponent component;

    public IntervalEditor(Interval initialData) {
        from = new UtilDateModel(Date.from(initialData.from().toInstant()));
        to = new UtilDateModel(Date.from(initialData.to().toInstant()));
        component = borderLayout()
                .north(fromWithLabel())
                .south(toWithLabel())
                .build();
    }

    private JComponent fromWithLabel() {
        return borderLayout().center(label("From:")).east(datePicker(from)).build();
    }

    private JComponent toWithLabel() {
        return borderLayout().center(label("To:")).east(datePicker(to)).build();
    }

    public JComponent getComponent() {
        return component;
    }

    public Interval getInterval() {
        return new Interval(
                toMidnight(ZonedDateTime.ofInstant(from.getValue().toInstant(), ZoneId.systemDefault())),
                toMidnight(ZonedDateTime.ofInstant(to.getValue().toInstant(), ZoneId.systemDefault()))
        );
    }

    @Override
    public Interval get() {
        return getInterval();
    }
}
