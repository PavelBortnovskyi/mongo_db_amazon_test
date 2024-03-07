package com.neo.mongocachetest.service;

import com.neo.mongocachetest.annotation.CascadeSave;
import com.neo.mongocachetest.model.BaseDocument;
import com.neo.mongocachetest.model.Report;
import com.neo.mongocachetest.model.SalesAndTrafficByAsin;
import com.neo.mongocachetest.model.SalesAndTrafficByDate;
import com.neo.mongocachetest.repository.SalesAndTrafficByAsinRepository;
import com.neo.mongocachetest.repository.SalesAndTrafficByDateRepository;
import com.neo.mongocachetest.utils.JSONFileParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class DBService {

    private final JSONFileParser jsonFileParser;
    private final Map<Class<?>, MongoRepository<?, ?>> repositoryMap;
    private final SalesAndTrafficByDateRepository salesAndTrafficByDateRepository;
    private final SalesAndTrafficByAsinRepository salesAndTrafficByAsinRepository;

    @PostConstruct
    @Scheduled(cron = "0 0/2 * * * *")
    public void loadDataFromLocalFile() {
        log.info("Loading data from file...");
        jsonFileParser.extractReportsFromFile().ifPresentOrElse(this::saveDataFromFile,
                () -> log.error("Something went wrong during loading data from local file"));
    }

    public void saveDataFromFile(Report report) {
        //Bad solution for production. Also, possible to use some boolean flag - initiated
        if (salesAndTrafficByDateRepository.findAll().isEmpty() && salesAndTrafficByAsinRepository.findAll().isEmpty()) {
            saveAssociatedEntities(report);
            log.info("Data loaded from file!");
        } else {
            updateAssociatedEntities(report);
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
                    } else if (repository != null) {
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

    private void updateAssociatedEntities(Report report) {
        String rewriteId = "";
        long addCounter = 0L;
        long updateCounter = 0L;
        SalesAndTrafficByDate dbRecord1;
        SalesAndTrafficByAsin dbRecord2;

        List<SalesAndTrafficByDate> salesAndTrafficByDates = report.getSalesAndTrafficByDate();
        List<SalesAndTrafficByAsin> salesAndTrafficByAsins = report.getSalesAndTrafficByAsin();

        for (SalesAndTrafficByDate salesAndTrafficByDate : salesAndTrafficByDates) {
            dbRecord1 = salesAndTrafficByDateRepository.findByDate(salesAndTrafficByDate.getDate()).orElseGet(null);
            if (dbRecord1 == null) {
                salesAndTrafficByDateRepository.save(salesAndTrafficByDate);
                addCounter++;
                continue;
            }
            if (!salesAndTrafficByDate.equals(dbRecord1)) {
                List<String> idList = extractIds(dbRecord1);
                setIds(idList, salesAndTrafficByDate);
                saveAssociatedEntities(salesAndTrafficByDate);
                salesAndTrafficByDate.setId(dbRecord1.getId());
                salesAndTrafficByDateRepository.save(salesAndTrafficByDate);
                updateCounter++;
            }
        }
        for (SalesAndTrafficByAsin salesAndTrafficByAsin : salesAndTrafficByAsins) {
            dbRecord2 = salesAndTrafficByAsinRepository.findByParentAsin(salesAndTrafficByAsin.getParentAsin()).orElseGet(null);
            if (dbRecord2 == null) {
                salesAndTrafficByAsinRepository.save(salesAndTrafficByAsin);
                addCounter++;
                continue;
            }
            if (!salesAndTrafficByAsin.equals(dbRecord2)) {
                List<String> idList = extractIds(dbRecord2);
                setIds(idList, salesAndTrafficByAsin);
                saveAssociatedEntities(salesAndTrafficByAsin);
                salesAndTrafficByAsin.setId(dbRecord2.getId());
                salesAndTrafficByAsinRepository.save(salesAndTrafficByAsin);
                updateCounter++;
            }
        }
        log.info("Documents added: " + addCounter);     //incorrect measuring (shows only top level docs)
        log.info("Updated documents " + updateCounter); //incorrect measuring (shows only top level docs)
    }

    private List<String> extractIds (BaseDocument doc) {
        Field[] fields = doc.getClass().getDeclaredFields();
        List<String> idList = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(doc);
                if (fieldValue instanceof BaseDocument) {
                    BaseDocument subDoc = (BaseDocument) fieldValue;
                    idList.add(subDoc.getId());
                }
                if (field.isAnnotationPresent(CascadeSave.class)) {
                    idList.addAll(extractIds((BaseDocument) fieldValue));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return idList;
    }

    private void setIds (List<String> idList, BaseDocument doc) {
       setIdsT(idList, doc, 0);
    }

    private int setIdsT (List<String> idList, BaseDocument doc, int index) {
        Field[] fields = doc.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(doc);
                if (fieldValue instanceof BaseDocument) {
                    BaseDocument subDoc = (BaseDocument) fieldValue;
                    subDoc.setId(idList.get(index));
                    index++;
                }
                if (field.isAnnotationPresent(CascadeSave.class)) {
                    index = setIdsT(idList, (BaseDocument) fieldValue, index);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return index;
    }
}
