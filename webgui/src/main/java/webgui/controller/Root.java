package webgui.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.ZonedDateTime;

@Controller
public class Root {

    @RequestMapping(value = "/")
    public String root(ModelMap model) {
        model.addAttribute("time", ZonedDateTime.now().toString());
        return "root";
    }
}
