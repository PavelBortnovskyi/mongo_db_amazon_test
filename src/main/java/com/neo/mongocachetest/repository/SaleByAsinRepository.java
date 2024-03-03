package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.SaleByAsin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleByAsinRepository extends MongoRepository<SaleByAsin, String> {
}
