package org.thinkbigthings.zdd.server;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.thinkbigthings.zdd.dto.UserDTO;

import java.util.stream.Stream;


@RestController
public class UserController {

    private final UserService service;

    // if there's only one constructor, can omit Autowired and Inject
    public UserController(UserService s) {
        service = s;
    }

    @RequestMapping(value="/user", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Stream<User> getAllUsers() {

        return service.getAllUsers();
    }

    @RequestMapping(value="/user", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User createUser(@RequestBody UserDTO newUser) {

        var user = new User(newUser.username, newUser.displayName);
        user.setEmail(newUser.email);

        return service.saveNewUser(user);
    }

    @RequestMapping(value="/user/{username}", method= RequestMethod.PUT, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User updateUser(@RequestBody User newUser, @PathVariable String username) {

        return service.updateUser(username, newUser);
    }

    @RequestMapping(value="/user/{username}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUser(@PathVariable String username) {

        return service.getUser(username);
    }
}