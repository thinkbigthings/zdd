package org.thinkbigthings.zdd;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(String name);

    Stream<User> findAllByEnabled(boolean enabled);

    default Stream<User> findAllAsStream() {
        return StreamSupport.stream(findAll().spliterator(), false);
    }

    @Query(nativeQuery = true, value = "select cast (gen_random_uuid() as varchar(36))")
    String generateUuidString();

    default UUID createUuid() {
        return UUID.fromString(generateUuidString());
    }

}
