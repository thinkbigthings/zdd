package org.thinkbigthings.zdd;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/user")
    public User getUser(@RequestParam(value="name", defaultValue="user") String name) {
        return new User(counter.incrementAndGet(), String.format(template, name));
    }
}