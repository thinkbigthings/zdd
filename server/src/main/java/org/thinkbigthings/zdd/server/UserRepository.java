package org.thinkbigthings.zdd.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String name);

    Stream<User> findAllByEnabled(boolean enabled);

    default Stream<User> findAllAsStream() {
        return StreamSupport.stream(findAll().spliterator(), false);
    }

    @Query(nativeQuery = true, value = "select cast (public.gen_random_uuid() as varchar(36))")
    String generateUuidString();

    default UUID createUuid() {
        return UUID.fromString(generateUuidString());
    }

}