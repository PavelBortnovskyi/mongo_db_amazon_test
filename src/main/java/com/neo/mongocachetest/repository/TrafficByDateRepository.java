package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.TrafficByDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficByDateRepository extends MongoRepository<TrafficByDate, String> {
}
