package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.SalesAndTrafficByDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesAndTrafficByDateRepository extends MongoRepository<SalesAndTrafficByDate, String> {
}
