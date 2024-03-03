package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.ProductSale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSaleRepository extends MongoRepository<ProductSale, String> {
}
