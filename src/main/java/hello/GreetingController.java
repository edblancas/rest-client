package hello;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class GreetingController {

    @Value("${application.message:Hello World!}")
    private String message = "";

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

        String oauthUrl = "https://api.twitter.com/oauth2/token";
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
        BearerToken token = restTemplate.postForObject(oauthUrl, entity, BearerToken.class);

        request.getSession().setAttribute("bearer", token.getAccess_token());
        request.getSession().setAttribute("username", username);
        return "redirect:/greeting";
    }

    @RequestMapping(value = "/login", method = GET)
    public String loginGET(Model model, HttpServletRequest request) {
        model.addAttribute("jsonRes", "{}");
        if (request.getSession(false) != null) {
            return "redirect:/greeting";
        }
        return "login";
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
                           Model model,
                           HttpServletRequest request) {
        model.addAttribute("jsonRes", "{}");
        if (request.getSession(false) == null) {
            model.addAttribute("jsonRes", "{\"message\": \"No tienes sesión!\"}");
        } else {
            model.addAttribute("jsonRes", "{\"message\": \"" + request.getSession().getAttribute("username") + "\"}");
        }
        return "greeting";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class NotFoundException extends RuntimeException {
    }

    @RequestMapping(value = "/twitter-list-followers", method = GET)
    public String twitterListFollowers(Model model, HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return "redirect:/greeting";
        }

            String url = "https://api.twitter.com/1.1/followers/list.json?cursor=-1&" +
                "screen_name=" + request.getSession().getAttribute("username") + "&skip_status=true&include_user_entities=false";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " +request.getSession().getAttribute("bearer"));

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
        HttpEntity<String> response= new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

        model.addAttribute("jsonRes", response.getBody());
    } catch (HttpClientErrorException e) {
        throw new NotFoundException();
    }
        return "twitter-list-followers";
    }

    @RequestMapping(value = "/twitter-list-friends", method = GET)
    public String twitterListFriends(Model model, HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return "redirect:/greeting";
        }
        String url = "https://api.twitter.com/1.1/friends/list.json?cursor=-1&screen_name=" + request.getSession().getAttribute("username") +
                "&skip_status=true&include_user_entities=false";

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<Object>(headers);
        RestTemplate restTemplate = new RestTemplate();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + request.getSession().getAttribute("bearer"));

        try {
            HttpEntity<String> response= new RestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<String>(headers), String.class);
            model.addAttribute("jsonRes", response.getBody());
        } catch (HttpClientErrorException e) {
            throw new NotFoundException();
        }

        return "twitter-list-friends";
    }

    @RequestMapping(value = "/twitter-list-lists", method = GET)
    public String twitterListLists(Model model, HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return "redirect:/greeting";
        }
        String url = "https://api.twitter.com/1.1/lists/list.json?screen_name=" + request.getSession().getAttribute("username");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String bearer = (String) request.getSession().getAttribute("bearer");
        headers.set("Authorization", "Bearer " + bearer);

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
        HttpEntity<String> response= new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

        model.addAttribute("jsonRes", response.getBody());
    } catch (HttpClientErrorException e) {
        throw new NotFoundException();
    }

        return "twitter-list-lists";
    }

}