package webgui.config;

import config.RemoteServices;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "webgui.json")
@Import(RemoteServices.class)
public class JsonServletConfig {
}
