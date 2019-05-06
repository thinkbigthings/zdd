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

    @Transactional
    public User saveNewUser(User newUser) {

        var user = new User(newUser.getUsername(), newUser.getDisplayName());

        user.setEmail(newUser.getEmail());


        // Hibernate doesn't update with the object's db-generated uuid when you flush since the object is still cached
        // need to implement refresh yourself with the spring data repo
        // or load in a new transaction so the repo selects from the database instead of finding from cache
        // if there's a "refresh" it seems like more work for the database to save and then select and then update the cache
        // than to just set the uuid and save it once.
        user.setExternalId(userRepo.createUuid());

        return userRepo.saveAndFlush(user);

    }

    @Transactional
    public Stream<User> getAllUsers() {

        return userRepo.findAllAsStream();
    }

    @Transactional
    public User getUser(String username) {

        return userRepo.findByUsername(username);
    }
}
