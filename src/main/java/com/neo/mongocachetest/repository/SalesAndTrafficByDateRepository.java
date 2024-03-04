package com.neo.mongocachetest.repository;

import com.neo.mongocachetest.model.SalesAndTrafficByDate;
import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesAndTrafficByDateRepository extends MongoRepository<SalesAndTrafficByDate, String> {

    Optional<SalesAndTrafficByDate> findByDate(Date date);

    List<SalesAndTrafficByDate> findByDateBetween(Date startDate, Date endDate);
}
