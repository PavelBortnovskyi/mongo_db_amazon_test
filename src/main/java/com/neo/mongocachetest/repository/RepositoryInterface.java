package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.BaseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositoryInterface<E extends BaseDocument> extends MongoRepository<E, String> {
}