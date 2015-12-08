package client.ui.util;

import schedule.view.ResourceRenderer;

public class CarResourceRenderer extends ResourceRenderer.Default<CarResource> {
    @Override
    protected String getTextFromResource(CarResource resource) {
        return resource.registration + " , " + resource.model;
    }
}
