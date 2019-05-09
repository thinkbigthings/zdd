package org.thinkbigthings.zdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {


    private UserRepository userRepo = Mockito.mock(UserRepository.class);

    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserService(userRepo);

        when(userRepo.save(any(User.class))).then(returnsFirstArg());
        when(userRepo.saveAndFlush(any(User.class))).then(returnsFirstArg());

        when(userRepo.createUuid()).thenReturn(UUID.randomUUID());
    }

    @Test
    public void createUser() {

        String name = "newuserhere";
        User newUser = new User(name, name);

        newUser.setEmail(name+"@email.com");

        User created = service.saveNewUser(newUser);

        assertEquals(name, created.getUsername());
    }

}
