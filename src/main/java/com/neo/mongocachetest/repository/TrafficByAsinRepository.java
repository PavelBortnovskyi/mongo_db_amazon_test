package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.TrafficByAsin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficByAsinRepository extends MongoRepository<TrafficByAsin, String> {
}
