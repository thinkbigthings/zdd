package org.thinkbigthings.zdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {


    private UserRepository userRepo = Mockito.mock(UserRepository.class);

    private UserController controller;

    @BeforeEach
    public void setup() {
        controller = new UserController(userRepo);

        when(userRepo.save(any(User.class))).then(returnsFirstArg());
    }

    @Test
    public void controllerCreatesUser() {

        String userName = "newuserhere";
        User newUser = new User();
        newUser.setUsername(userName);
        newUser.setDisplayName(userName);
        newUser.setEmail(userName+"@email.com");

        User created = controller.createUser(newUser);

        assertEquals(userName, created.getUsername());
    }

}
