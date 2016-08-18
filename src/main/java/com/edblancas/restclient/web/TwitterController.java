package com.edblancas.restclient.web;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class TwitterController {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class NotFoundException extends RuntimeException {
    }

    @RequestMapping(value = "/twitter-list-followers", method = GET)
    public String twitterListFollowers(Model model, HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return "redirect:/home";
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
            return "redirect:/home";
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
            return "redirect:/home";
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
