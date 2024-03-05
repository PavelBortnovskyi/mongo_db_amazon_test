package com.neo.mongocachetest.service;

import com.neo.mongocachetest.dto.*;
import com.neo.mongocachetest.enums.Granularity;
import com.neo.mongocachetest.enums.ReportType;
import com.neo.mongocachetest.repository.SalesAndTrafficByAsinRepository;
import com.neo.mongocachetest.repository.SalesAndTrafficByDateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportService {

    private final SalesAndTrafficByDateRepository salesAndTrafficByDateRepository;
    private final SalesAndTrafficByAsinRepository salesAndTrafficByAsinRepository;
    private final ModelMapper mm;

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

    public ReportDTO getAllReportsByDate() {
        ReportDTO report = this.getReportWithHeader();
        ReportSpecificationDTO reportSpecification = report.getReportSpecification();
        report.setSalesAndTrafficByDate(salesAndTrafficByDateRepository.findAll()
                .stream()
                .map(s -> mm.map(s, SalesAndTrafficByDateDTO.class))
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

    public ReportDTO getAllReportByAsin() {
        ReportDTO report = this.getReportWithHeader();
        ReportSpecificationDTO reportSpecification = report.getReportSpecification();
        report.setSalesAndTrafficByAsin(salesAndTrafficByAsinRepository.findAll()
                .stream()
                .map(s -> mm.map(s, SalesAndTrafficByAsinDTO.class))
                .collect(Collectors.toList()));
        report.setReportSpecification(reportSpecification);
        return report;
    }

    public ReportDTO getReportByAsin(String ASIN) {
        ReportDTO report = this.getReportWithHeader();
        report.setSalesAndTrafficByAsin(new ArrayList<>() {{
            add(salesAndTrafficByAsinRepository.findByParentAsin(ASIN)
                    .map(d -> mm.map(d, SalesAndTrafficByAsinDTO.class))
                    .orElse(null));
        }});
        return report;
    }

    public ReportDTO getReportByAsinList(List<String> ASINs) {
        ReportDTO report = this.getReportWithHeader();
        report.setSalesAndTrafficByAsin(salesAndTrafficByAsinRepository.findByParentAsinIn(ASINs)
                .stream().map(d -> mm.map(d, SalesAndTrafficByAsinDTO.class)).collect(Collectors.toList()));
        return report;
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
