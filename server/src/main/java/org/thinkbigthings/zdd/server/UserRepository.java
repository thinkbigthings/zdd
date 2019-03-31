package org.thinkbigthings.zdd.server;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String name);

    default Page<User> findRecent() {
        return findAll(PageRequest.of(0, 5, Sort.by(Sort.Order.desc("registration"))));
    }

}
