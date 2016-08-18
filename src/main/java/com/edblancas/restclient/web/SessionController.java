package com.edblancas.restclient.web;

import com.edblancas.restclient.api.TwitterApi;
import com.edblancas.restclient.models.BearerToken;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class SessionController {
    @Autowired
    TwitterApi twitterApi;

    @Value("${application.consumerKey}")
    private String CONSUMER_KEY = "";
    @Value("${application.consumerSecret}")
    private String CONSUMER_SECRET = "";

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

        final String KEY_SECRET = CONSUMER_KEY + ":" + CONSUMER_SECRET;

        String authorizationString = "Basic " + Base64.getEncoder().encodeToString(
                KEY_SECRET.getBytes());

        HttpHeaders headers = new HttpHeaders();
        // note: tenia header accept json y no fucionaba
        headers.set("Authorization", authorizationString);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "client_credentials");

        HttpEntity<?> entity = new HttpEntity<Object>(map, headers);

        RestTemplate restTemplate = new RestTemplate();

        // note: con una clase interna no funciona
        BearerToken token = restTemplate.postForObject(twitterApi.AUTH_URL, entity, BearerToken.class);

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
