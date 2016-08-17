package hello;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import java.util.Date;
import java.util.Map;

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
}