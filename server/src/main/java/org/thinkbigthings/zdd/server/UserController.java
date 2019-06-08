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
    public Stream<UserDTO> getUsers() {

        return service.getUsers().get().map(this::toDto);
    }

    @RequestMapping(value="/user", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserDTO createUser(@RequestBody UserDTO newUser) {

        return toDto(service.saveNewUser(fromDto(newUser)));
    }

    @RequestMapping(value="/user/{username}", method= RequestMethod.PUT, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserDTO updateUser(@RequestBody UserDTO newUser, @PathVariable String username) {

        return toDto(service.updateUser(username, fromDto(newUser)));
    }

    @RequestMapping(value="/user/{username}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserDTO getUser(@PathVariable String username) {

        return toDto(service.getUser(username));
    }

    public User fromDto(UserDTO userData) {
        var user = new User(userData.username, userData.displayName);
        user.setEmail(userData.email);
        user.setPhoneNumber(userData.phoneNumber);
        user.setHeight(userData.height);
        return user;
    }

    public UserDTO toDto(User user) {
        UserDTO userData = new UserDTO();
        userData.displayName = user.getDisplayName();
        userData.email = user.getEmail();
        userData.username = user.getUsername();
        userData.registration = user.getRegistration().toString();
        userData.phoneNumber = user.getPhoneNumber();
        userData.height = user.getHeight();
        return userData;
    }
}