package org.thinkbigthings.zdd;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.stream.Stream;


@Service
@Transactional
public class UserService {


    private UserRepository userRepo;

    @Inject
    public UserService(UserRepository repo) {
        userRepo = repo;
    }

    public User saveNewUser(User newUser) {

        var user = new User(newUser.getUsername(), newUser.getDisplayName());
        user.setEmail(newUser.getEmail());

        return userRepo.save(user);
    }

    public Stream<User> getAllUsers() {

        return userRepo.findAllAsStream();
    }


    public User getUser(String username) {

        return userRepo.findByUsername(username);
    }
}
