package org.thinkbigthings.zdd.server;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class UserController {

    private final UserService service;

    // if there's only one constructor, can omit Autowired and Inject
    public UserController(UserService s) {
        service = s;
    }

    @RequestMapping(value="/user", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserDTO> getUsers() {

        return service.getUsers().stream().map(this::toDto).collect(Collectors.toList());
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
        user.setHeightCm(userData.heightCm);

        userData.addresses.stream()
                .map(this::fromDto)
                .peek(a -> a.setUser(user))
                .collect(Collectors.toCollection(() -> user.getAddresses()));

        return user;
    }

    public UserDTO toDto(User user) {

        var userData = new UserDTO();

        userData.displayName = user.getDisplayName();
        userData.email = user.getEmail();
        userData.username = user.getUsername();
        userData.registrationTime = user.getRegistrationTime().toString();
        userData.phoneNumber = user.getPhoneNumber();
        userData.heightCm = user.getHeightCm();

        user.getAddresses().stream()
                .map(this::toDto)
                .collect(Collectors.toCollection(() -> userData.addresses));

        return userData;
    }

    public Address fromDto(AddressDTO addressData) {

        var address = new Address();

        address.setLine1(addressData.line1);
        address.setCity(addressData.city);
        address.setState(addressData.state);
        address.setZip(addressData.zip);

        return address;
    }

    public AddressDTO toDto(Address address) {

        var addressData = new AddressDTO();

        addressData.line1 = address.getLine1();
        addressData.city = address.getCity();
        addressData.state = address.getState();
        addressData.zip = address.getZip();

        return addressData;
    }

}