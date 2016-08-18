package com.edblancas.restclient.api;

import com.edblancas.restclient.models.BearerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service("twitterApi")
public class TwitterApi {
    @Value("${application.twitter.apiUrl}")
    public String API_URL = "";
    @Value("${application.twitter.authUrl}")
    public String AUTH_URL = "";
    @Value("${application.consumerKey}")
    public String CONSUMER_KEY = "";
    @Value("${application.consumerSecret}")
    public String CONSUMER_SECRET = "";


    public BearerToken login() {
        final String KEY_SECRET = CONSUMER_KEY + ":" + CONSUMER_SECRET;
        String authorizationString = "Basic " + Base64.getEncoder().encodeToString(
                KEY_SECRET.getBytes());

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        headers.set("Authorization", authorizationString);
        map.add("grant_type", "client_credentials");
        HttpEntity<?> entity = new HttpEntity<Object>(map, headers);

        return restTemplate.postForObject(AUTH_URL, entity, BearerToken.class);
    }

}
