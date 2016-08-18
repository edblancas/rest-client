package com.edblancas.restclient.web;

import com.edblancas.restclient.api.TwitterApi;
import com.edblancas.restclient.models.BearerToken;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class SessionController {

    @Autowired
    private TwitterApi twitterApi;

    @RequestMapping(value="/logout", method = GET)
    public String logoutPage (HttpServletRequest request) {
        request.getSession().invalidate();
        return "logout";
    }

    @RequestMapping(value = "/login", method = POST)
    public String login(@RequestParam(value = "username", required = false) String username,
                        Model model,
                        HttpServletRequest request) {
        model.addAttribute("jsonRes", "{}");
        if (username.isEmpty()) {
            model.addAttribute("jsonRes", "{\"message\": \"Se requiere un usuario!\"}");
            return "login";
        }

        BearerToken token = twitterApi.login();
        request.getSession().setAttribute("bearer", token.getAccess_token());
        request.getSession().setAttribute("username", username);
        return "redirect:/home";
    }

    @RequestMapping(value = "/login", method = GET)
    public String loginGET(Model model, HttpServletRequest request) {
        model.addAttribute("jsonRes", "{}");
        if (request.getSession(false) != null) {
            return "redirect:/home";
        }
        return "login";
    }

}
