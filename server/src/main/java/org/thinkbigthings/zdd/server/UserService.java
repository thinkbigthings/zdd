package org.thinkbigthings.zdd.server;

import org.springframework.data.domain.Page;
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
        user.setFavoriteColor(userData.getFavoriteColor());
        user.setPhoneNumber(userData.getPhoneNumber());
        user.setHeight(userData.getHeight());

        return userRepo.save(user);
    }

    public User saveNewUser(User user) {

        user.setRegistration(Instant.now());
        user.setEnabled(true);

        return userRepo.save(user);
    }

    public Page<User> getUsers() {

        return userRepo.findRecent();
    }

    public User getUser(String username) {

        return userRepo.findByUsername(username);
    }
}
