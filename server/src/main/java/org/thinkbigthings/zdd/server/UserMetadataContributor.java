package org.thinkbigthings.zdd.server;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserMetadataContributor implements InfoContributor  {

    private UserRepository userRepository;

    public UserMetadataContributor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void contribute(Info.Builder builder) {

        var userMetadata = Map.of("count", String.valueOf(userRepository.count()));

        builder.withDetail("users", userMetadata);
    }
}
