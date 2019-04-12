package org.thinkbigthings.zdd;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Stream<User> findAllByEnabled(boolean enabled);

    default Stream<User> findAllAsStream() {
        return StreamSupport.stream(findAll().spliterator(), false);
    }
}
