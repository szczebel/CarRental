package webgui.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@SuppressWarnings("unused")
public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {


    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{
                ViewServletConfig.class,
                JsonServletConfig.class,
                SecurityConfiguration.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[0];

    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{
                "/view/*",
                "/json/*",
                "/register",
        };
    }
}
