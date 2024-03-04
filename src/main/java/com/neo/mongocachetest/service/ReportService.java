package com.neo.mongocachetest.service;

import com.neo.mongocachetest.annotation.CascadeSave;
import com.neo.mongocachetest.dto.*;
import com.neo.mongocachetest.enums.Granularity;
import com.neo.mongocachetest.enums.ReportType;
import com.neo.mongocachetest.model.ReportOptions;
import com.neo.mongocachetest.model.ReportSpecification;
import com.neo.mongocachetest.model.SalesAndTrafficByDate;
import com.neo.mongocachetest.repository.ReportRepository;
import com.neo.mongocachetest.model.Report;
import com.neo.mongocachetest.repository.SaleByDateRepository;
import com.neo.mongocachetest.repository.SalesAndTrafficByAsinRepository;
import com.neo.mongocachetest.repository.SalesAndTrafficByDateRepository;
import com.neo.mongocachetest.utils.JSONFileParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportService {

    private final JSONFileParser jsonFileParser;

    private final SalesAndTrafficByDateRepository salesAndTrafficByDateRepository;

    private final SalesAndTrafficByAsinRepository salesAndTrafficByAsinRepository;

    private final Map<Class<?>, MongoRepository<?, ?>> repositoryMap;

    private final ModelMapper mm;

    private String lastId = "";

    @PostConstruct
    //@Scheduled(cron = "0 0/1 * * * *")
    public void loadDataFromLocalFile() {
        log.info("Loading data from file...");
        jsonFileParser.extractReportsFromFile().ifPresentOrElse(this::saveReport,
                () -> log.error("Something went wrong during loading data from local file"));
    }

    public ReportDTO getTotalReport() {
        ReportDTO report = this.getReportWithHeader();
        ReportSpecificationDTO reportSpecification = report.getReportSpecification();
        report.setSalesAndTrafficByDate(salesAndTrafficByDateRepository.findAll()
                .stream()
                .map(s -> mm.map(s, SalesAndTrafficByDateDTO.class))
                .collect(Collectors.toList()));
        report.setSalesAndTrafficByAsin(salesAndTrafficByAsinRepository.findAll()
                .stream()
                .map(s -> mm.map(s, SalesAndTrafficByAsinDTO.class))
                .collect(Collectors.toList()));
        reportSpecification.setDataStartTime(report.getSalesAndTrafficByDate().get(0).getDate());
        reportSpecification.setDataEndTime(report.getSalesAndTrafficByDate().get(report.getSalesAndTrafficByDate().size() - 1).getDate());
        report.setReportSpecification(reportSpecification);
        return report;
    }

    public ReportDTO getReportByDate(Date date) {
        ReportDTO report = this.getReportWithHeader();
        report.getReportSpecification().setDataStartTime(date);
        report.getReportSpecification().setDataEndTime(date);
        report.setSalesAndTrafficByDate(new ArrayList<>() {{
            add(salesAndTrafficByDateRepository.findByDate(date)
                    .map(d -> mm.map(d, SalesAndTrafficByDateDTO.class))
                    .orElse(null));
        }});
        return report;
    }

    public ReportDTO getReportByDateRange(Date startDate, Date endDate) {
        ReportDTO report = this.getReportWithHeader();

        report.getReportSpecification().setDataStartTime(startDate);
        report.getReportSpecification().setDataEndTime(endDate);

        LocalDate startDateInclusive = Instant.ofEpochMilli(startDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .minusDays(1);
        LocalDate endDateInclusive = Instant.ofEpochMilli(endDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(1);
        startDate = Date.from(startDateInclusive.atStartOfDay(ZoneId.systemDefault()).toInstant());
        endDate = Date.from(endDateInclusive.atStartOfDay(ZoneId.systemDefault()).toInstant());

        report.setSalesAndTrafficByDate(salesAndTrafficByDateRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map((d) -> mm.map(d, SalesAndTrafficByDateDTO.class))
                .collect(Collectors.toList()));
        return report;
    }


    public void saveReport(Report report) {
        if (lastId.isEmpty()) {
            saveAssociatedEntities(report);
            lastId = report.getId();
            log.info("Data loaded from file!");
        } else {
            report.setId(lastId);
            saveAssociatedEntities(report);
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

    public ReportDTO getReportWithHeader() {
        ReportDTO report = new ReportDTO();
        ReportSpecificationDTO reportSpecification = new ReportSpecificationDTO();
        ReportOptionsDTO reportOptions = new ReportOptionsDTO();
        reportOptions.setAsinGranularity("PARENT");
        reportOptions.setDateGranularity(Granularity.DAY);
        reportSpecification.setReportOptions(reportOptions);
        reportSpecification.setReportType(ReportType.GET_SALES_AND_TRAFFIC_REPORT);
        reportSpecification.setMarketplaceIds(List.of("ATVPDKIKX0DER"));
        report.setReportSpecification(reportSpecification);
        return report;
    }
}
