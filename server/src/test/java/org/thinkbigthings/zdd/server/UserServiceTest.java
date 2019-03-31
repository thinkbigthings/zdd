package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {


    private UserRepository userRepo = Mockito.mock(UserRepository.class);

    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserService(userRepo);

        Mockito.when(userRepo.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(userRepo.saveAndFlush(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());

    }

    @Test
    public void createUser() {

        String name = "newuserhere";
        User newUser = new User(name);

        newUser.setEmail(name+"@email.com");

        User created = service.saveNewUser(newUser);

        assertEquals(name, created.getUsername());
    }

}
