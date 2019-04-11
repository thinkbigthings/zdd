package org.thinkbigthings.zdd;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class UserController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    private UserRepository userRepo;

    @Inject
    public UserController(UserRepository repo) {
        userRepo = repo;
    }

    @RequestMapping("/user")
    public User getUser(@RequestParam(value="name", defaultValue="user") String name) {

        long count = userRepo.count();
        String userName = String.format(template, name);
        User user = new User(userName, userName + count);
        user.setEmail(name + "@" + name + ".com");

        return userRepo.save(user);
    }
}