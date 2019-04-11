package org.thinkbigthings.zdd;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class UserController {

    private UserRepository userRepo;

    @Inject
    public UserController(UserRepository repo) {
        userRepo = repo;
    }

    @RequestMapping("/user")
    public User createUser(@RequestParam(value="name", defaultValue="user") String name) {

        long count = userRepo.count();

        var user = new User(name, name + count);
        user.setEmail(name + "@" + name + ".com");

        return userRepo.save(user);
    }
}