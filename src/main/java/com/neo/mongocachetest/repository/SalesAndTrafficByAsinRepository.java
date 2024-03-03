package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.SalesAndTrafficByAsin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesAndTrafficByAsinRepository extends MongoRepository<SalesAndTrafficByAsin, String> {
}
