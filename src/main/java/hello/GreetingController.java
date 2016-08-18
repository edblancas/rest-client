package hello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class GreetingController {

    @Value("${application.message:Hello World}")
    private String message = "Hello World";

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @RequestMapping(value = "/quote", method = GET)
    public String quote(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        Quote quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", Quote.class);
        model.addAttribute("quote", quote);
        return "quote";
    }

    @GetMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", this.message);
        return "welcome";
    }

    @RequestMapping("/serviceUnavailable")
    public String ServiceUnavailable() {
        throw new ServiceUnavailableException();
    }

    @RequestMapping("/bang")
    public String bang() {
        throw new RuntimeException("Boom");
    }

    @RequestMapping("/insufficientStorage")
    public String insufficientStorage() {
        throw new InsufficientStorageException();
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    private static class ServiceUnavailableException extends RuntimeException {
    }

    @ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
    private static class InsufficientStorageException extends RuntimeException {
    }

    @RequestMapping(value = "/twitter-list-followers", method = GET)
    public String twitterListFollowers(Model model) {
        String url = "https://api.twitter.com/1.1/followers/list.json?cursor=-1&" +
                "screen_name=srpagooficial&skip_status=true&include_user_entities=false";
        String accessToken = "AAAAAAAAAAAAAAAAAAAAAIBNwgAAAAAA0zJztq8trtYg3jdfAkh5ulr8F2s%3DzciLfkNPobaaqXoN6cynGrQWYsqxivn1Z94E7w5VLPsxEjAO7p";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        HttpEntity<String> response= new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

        model.addAttribute("jsonRes", response.getBody());

        return "twitter-list-followers";
    }

    @RequestMapping(value = "/twitter-list-friends", method = GET)
    public String twitterListFriends(Model model, HttpServletRequest request) {
        String url = "https://api.twitter.com/1.1/friends/list.json?cursor=-1&screen_name=srpagooficial" +
                "&skip_status=true&include_user_entities=false";
        String oauthUrl = "https://api.twitter.com/oauth2/token";
        final String CONSUMER_KEY = "D3BCkQgL8Ve47Sw0HVs9sZ0ec";
        final String CONSUMER_SECRET = "uBN3p1AmFnkG87x2JG0ybOg0fvUQFZhgpEBGg6jf7IEC2kMYhk";
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

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token.getAccess_token());
        HttpEntity<String> response= new RestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<String>(headers), String.class);
        model.addAttribute("jsonRes", response.getBody());

        return "twitter-list-friends";
    }

    @RequestMapping(value = "/twitter-list-lists", method = GET)
    public String twitterListLists(Model model, HttpServletRequest request) {
        String url = "https://api.twitter.com/1.1/lists/list.json?screen_name=twitterapi";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String bearer = (String) request.getSession().getAttribute("bearer");
        headers.set("Authorization", "Bearer " + bearer);

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        HttpEntity<String> response= new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

        model.addAttribute("jsonRes", response.getBody());

        return "twitter-list-lists";
    }

    @RequestMapping(value="/logout", method = GET)
    public String logoutPage (HttpServletRequest request) {
      request.getSession().invalidate();
        return "logout";
    }
}