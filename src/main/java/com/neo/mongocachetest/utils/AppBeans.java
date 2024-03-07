package com.neo.mongocachetest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neo.mongocachetest.constants.Parameters;
import com.neo.mongocachetest.dto.response.ReportDTO;
import com.neo.mongocachetest.model.*;
import com.neo.mongocachetest.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppBeans {

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        // Skip properties with null value
        mm.getConfiguration().setPropertyCondition(u -> u.getSource() != null);
        return mm;
    }

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList("fullReport", "allDatesReports", "specificDateReport",
                "dataRangeReport", "asinReport", "specificAsinReport", "asinListReport"));
        return cacheManager;
    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Map<Class<?>, MongoRepository<?, ?>> repositoryMap(ReportRepository reportRepository,
                                                              ReportSpecificationRepository reportSpecificationRepository,
                                                              ReportOptionsRepository reportOptionsRepository,
                                                              ProductSaleRepository productSaleRepository,
                                                              SalesAndTrafficByDateRepository salesAndTrafficByDateRepository,
                                                              SalesAndTrafficByAsinRepository salesAndTrafficByAsinRepository,
                                                              SaleByDateRepository saleByDateRepository,
                                                              SaleByAsinRepository saleByAsinRepository,
                                                              TrafficByDateRepository trafficByDateRepository,
                                                              TrafficByAsinRepository trafficByAsinRepository) {
        Map<Class<?>, MongoRepository<?, ?>> map = new HashMap<>();
        //map.put(Report.class, reportRepository);
        //map.put(ReportSpecification.class, reportSpecificationRepository);
        //map.put(ReportOptions.class, reportOptionsRepository);
        map.put(ProductSale.class, productSaleRepository);
        map.put(SalesAndTrafficByDate.class, salesAndTrafficByDateRepository);
        map.put(SalesAndTrafficByAsin.class, salesAndTrafficByAsinRepository);
        map.put(SaleByDate.class, saleByDateRepository);
        map.put(SaleByAsin.class, saleByAsinRepository);
        map.put(TrafficByDate.class, trafficByDateRepository);
        map.put(TrafficByAsin.class, trafficByAsinRepository);
        return map;
    }
}
