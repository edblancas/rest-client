package hello;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @RequestMapping("/home")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
                           Model model,
                           HttpServletRequest request) {
        model.addAttribute("jsonRes", "{}");
        if (request.getSession(false) == null) {
            model.addAttribute("jsonRes", "{\"message\": \"No tienes sesión!\"}");
        } else {
            model.addAttribute("jsonRes", "{\"message\": \"" + request.getSession().getAttribute("username") + "\"}");
        }
        return "home";
    }

}