package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.SaleByDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleByDateRepository extends MongoRepository<SaleByDate, String> {
}
