package com.edblancas.restclient.api;

import com.edblancas.restclient.Application;
import com.edblancas.restclient.models.BearerToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext
public class TwitterApiTests {
    @Autowired
    TwitterApi twitterApi;

    @Test
    public void loginTest() throws Exception {
        BearerToken token = twitterApi.login();
        assertThat(token.getAccess_token().isEmpty(), is(false));
    }

    @Test
    public void getFollwersTest() throws Exception {
        BearerToken token = twitterApi.login();
        HttpEntity<String> response = twitterApi.getFollowers("edblancas", token.getAccess_token());
        assertThat(response.getBody().contains("Meganopal"), is(true));
    }
}
