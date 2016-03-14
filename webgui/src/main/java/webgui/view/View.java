package webgui.view;


import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class View {

    @Autowired ClientService clientService;

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(String email, String name, HttpServletResponse resp) throws IOException {
        clientService.create(new Client(name, email));
        resp.sendRedirect("/view/home");
    }
}

