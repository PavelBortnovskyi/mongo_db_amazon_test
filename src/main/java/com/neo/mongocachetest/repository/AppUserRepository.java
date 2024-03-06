package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.AppUser;
import com.neo.mongocachetest.model.ProductSale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByRefreshToken(String refreshToken);
}
