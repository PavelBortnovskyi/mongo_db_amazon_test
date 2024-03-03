package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.ReportSpecification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportSpecificationRepository extends MongoRepository<ReportSpecification, String> {
}
