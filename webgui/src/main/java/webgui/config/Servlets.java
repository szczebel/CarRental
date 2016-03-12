package webgui.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@SuppressWarnings("unused")
public class Servlets extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{
                ViewServletConfig.class,
                JsonServletConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{
                "/view/*",
                "/json/*"
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[0];
    }
}
