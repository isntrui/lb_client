package ru.isntrui.store.mikrotest_client;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class TestController {
    @GetMapping("/test/{name}")
    public String test(@RequestParam String name) {
        return "Hello, " + name;
    }
}
