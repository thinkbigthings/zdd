package org.thinkbigthings.zdd.server;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.stream.Stream;


@Service
@Transactional
public class UserService {


    private final UserRepository userRepo;

    public UserService(UserRepository repo) {
        userRepo = repo;
    }

    public User updateUser(String username, User userData) {

        var user = userRepo.findByUsername(username);

        user.setEmail(userData.getEmail());
        user.setDisplayName(userData.getDisplayName());

        return userRepo.save(user);
    }

    public User saveNewUser(User newUser) {

        var user = new User(newUser.getUsername(), newUser.getDisplayName());

        user.setEmail(newUser.getEmail());

        user.setRegistration(Instant.now());

        // Hibernate doesn't update with the object's db-generated uuid when you flush since the object is still cached
        // need to implement refresh yourself with the spring data repo
        // or load in a new transaction so the repo selects from the database instead of finding from cache
        // if there's a "refresh" it seems like more work for the database to save and then select and then update the cache
        // than to just set the uuid and save it once.
        user.setExternalId(userRepo.createUuid());

        return userRepo.saveAndFlush(user);

    }

    public Stream<User> getAllUsers() {

        return userRepo.findAllAsStream();
    }

    public User getUser(String username) {

        return userRepo.findByUsername(username);
    }
}
