package client.ui.util;

import common.util.Interval;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static client.ui.util.GuiHelper.datePicker;
import static client.ui.util.GuiHelper.simpleForm;
import static common.util.TimeUtils.toMidnight;

public class IntervalEditor {

    protected final UtilDateModel from;
    protected final UtilDateModel to;

    public IntervalEditor(Interval interval) {
        from = new UtilDateModel(Date.from(interval.from().toInstant()));
        to = new UtilDateModel(Date.from(interval.to().toInstant()));
    }

    public JComponent createComponent() {
        return simpleForm()
                .addRow("From:", datePicker(from))
                .addRow("To:",   datePicker(to))
                .build();
    }

    public Interval getInterval() {
        return new Interval(
                toMidnight(ZonedDateTime.ofInstant(from.getValue().toInstant(), ZoneId.systemDefault())),
                toMidnight(ZonedDateTime.ofInstant(to.getValue().toInstant(), ZoneId.systemDefault()))
        );
    }
}
