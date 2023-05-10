package com.mitocode.repo;

import com.mitocode.model.User;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Mono;

public interface IUserRepo extends IGenericRepo<User, String>{

    //@Query("{username: ?}")
    Mono<User> findOneByUsername(String username);
}
