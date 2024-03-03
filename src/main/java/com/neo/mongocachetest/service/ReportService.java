package com.neo.mongocachetest.service;

import com.neo.mongocachetest.annotation.CascadeSave;
import com.neo.mongocachetest.repository.ReportRepository;
import com.neo.mongocachetest.model.Report;
import com.neo.mongocachetest.utils.JSONFileParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportService {

    private final JSONFileParser jsonFileParser;

    private final ReportRepository reportRepository;

    private final Map<Class<?>, MongoRepository<?, ?>> repositoryMap;

    private String lastId = "";

    @PostConstruct
    //@Scheduled(cron = "0 0/1 * * * *")
    public void loadDataFromLocalFile() {
        log.info("Loading data from file...");
        log.info(jsonFileParser.extractReportsFromFile().get().getReportSpecification().getDataStartTime());
        log.info(jsonFileParser.extractReportsFromFile().get().getReportSpecification().getDataEndTime());
        jsonFileParser.extractReportsFromFile().ifPresentOrElse(this::saveReport,
                () -> log.error("Something went wrong during loading data from local file"));
    }

    public void saveReport(Report report) {
        if (lastId.isEmpty()) {
            saveAssociatedEntities(report);
            report = reportRepository.save(report);
            lastId = report.getId();
            log.info("Data loaded from file!");
        } else {
            report.setId(lastId);
            saveAssociatedEntities(report);
            reportRepository.save(report);
            log.info("Data updated from file!");
        }
    }

    private void saveAssociatedEntities(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        MongoRepository<Object, String> repository;
        List<?> list = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(entity);
                if (fieldValue instanceof List) {
                    list = (List<?>) fieldValue;
                    repository = (MongoRepository<Object, String>) repositoryMap.get(list.get(0).getClass());
                } else repository = (MongoRepository<Object, String>) repositoryMap.get(field.getType());

                if (field.isAnnotationPresent(CascadeSave.class)) {
                    if (!list.isEmpty()) {
                        for (Object obj : list) {
                            saveAssociatedEntities(obj);
                            repository.save(obj);
                        }
                    } else {
                        saveAssociatedEntities(fieldValue);
                        repository.save(fieldValue);
                    }
                } else {
                    if (fieldValue != null) {
                        if (repository != null) {
                            if (!list.isEmpty()) {
                                for (Object obj : list) {
                                    repository.save(obj);
                                }
                            } else {
                                repository.save(fieldValue);
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
