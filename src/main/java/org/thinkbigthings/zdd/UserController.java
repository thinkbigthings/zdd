package org.thinkbigthings.zdd;

import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class UserController {

    private UserService service;

    @Inject
    public UserController(UserService s) {
        service = s;
    }

    @RequestMapping(value="/user", method=GET, produces=APPLICATION_JSON_VALUE)
    @ResponseBody
    public Stream<User> getAllUsers() {

        return service.getAllUsers();
    }

    @RequestMapping(value="/user", method=POST, produces=APPLICATION_JSON_VALUE)
    @ResponseBody
    public User createUser(@RequestBody User newUser) {

        var user = new User(newUser.getUsername(), newUser.getDisplayName());
        user.setEmail(newUser.getEmail());

        return service.saveNewUser(newUser);
    }

    @RequestMapping(value="/user/{username}", method=GET, produces=APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUser(@PathVariable String username) {

        return service.getUser(username);
    }
}