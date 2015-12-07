package client.ui.fullscheduleview;

import schedule.view.TaskRenderer;

import javax.swing.*;
import java.awt.*;

public class AbstractAssignmentRenderer<T extends AbstractAssignmentAsTask> extends TaskRenderer.Default<T> {

    @Override
    public JComponent getRenderingComponent(T task) {
        if(AbstractAssignmentAsTask.Type.HISTORICAL == task.getType()) setBackground(Color.pink);
        if(AbstractAssignmentAsTask.Type.CURRENT == task.getType()) setBackground(Color.cyan);
        if(AbstractAssignmentAsTask.Type.BOOKING == task.getType()) setBackground(Color.green);
        setOpaque(true);
        return super.getRenderingComponent(task);
    }

    @Override
    protected String getTextFromTask(T event) {
        return event.getAbstractAssignment().getClientName();
    }
}
