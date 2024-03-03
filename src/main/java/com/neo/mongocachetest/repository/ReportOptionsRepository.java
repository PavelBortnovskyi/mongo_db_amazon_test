package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.ReportOptions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportOptionsRepository extends MongoRepository<ReportOptions, String> {
}
