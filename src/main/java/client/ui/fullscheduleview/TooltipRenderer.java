package client.ui.fullscheduleview;

import schedule.view.TaskRenderer;

public class TooltipRenderer<T extends AbstractAssignmentAsTask> extends TaskRenderer.Default<T> {
    @Override
    protected String getTextFromTask(T event) {
        return "<html>" + event.getAbstractAssignment().getClientName() +
                "<p>" + event.getAbstractAssignment().getClientEmail() +
                "<p>" + event.getAbstractAssignment().getModel();
    }}
