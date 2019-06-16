package org.thinkbigthings.zdd.server;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserService {


    private final UserRepository userRepo;

    public UserService(UserRepository repo) {
        userRepo = repo;
    }

    public User updateUser(String username, User userData) {

        var user = getUser(username);

        user.setEmail(userData.getEmail());
        user.setDisplayName(userData.getDisplayName());
        user.setPhoneNumber(userData.getPhoneNumber());
        user.setHeightCm(userData.getHeightCm());

        user.getAddresses().forEach(a -> a.setUser(null));
        user.getAddresses().clear();

        user.getAddresses().addAll(userData.getAddresses());
        user.getAddresses().forEach(a -> a.setUser(user));

        return userRepo.save(user);
    }

    public User saveNewUser(User user) {

        user.setRegistrationTime(Instant.now());
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
