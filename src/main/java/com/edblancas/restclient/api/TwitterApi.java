package com.edblancas.restclient.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwitterApi {
    @Value("${application.twitter.apiUrl}")
    public String API_URL = "";
    @Value("${application.twitter.authUrl}")
    public String AUTH_URL = "";
    @Value("${application.consumerKey}")
    public String CONSUMER_KEY = "";
    @Value("${application.consumerSecret}")
    public String CONSUMER_SECRET = "";

}
