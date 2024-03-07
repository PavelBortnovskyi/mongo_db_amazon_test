package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.SalesAndTrafficByAsin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesAndTrafficByAsinRepository extends MongoRepository<SalesAndTrafficByAsin, String> {

    Optional<SalesAndTrafficByAsin> findByParentAsin(String parentAsin);

    List<SalesAndTrafficByAsin> findByParentAsinIn(List<String> parentAsinList);
}
