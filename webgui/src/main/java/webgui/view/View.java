package webgui.view;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class View {

    @RequestMapping("/home")
    public String home() {
        return "home";
    }
}

