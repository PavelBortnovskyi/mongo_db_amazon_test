package com.neo.mongocachetest.controller;

import com.neo.mongocachetest.constants.Parameters;
import com.neo.mongocachetest.dto.ReportDTO;
import com.neo.mongocachetest.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Log4j2
@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    private final CacheManager cacheManager;

    @Cacheable("testCache")
    @GetMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByDate() {
        return ResponseEntity.ok(reportService.getTotalReport());
    }

    @Cacheable("testCache")
    @GetMapping(value = "/reports/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByDateNotSpecified() {
        return ResponseEntity.ok(reportService.getAllReportsByDate());
    }

    @Cacheable("testCache")
    @GetMapping(value = "/reports/date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByDate(@PathVariable(value = Parameters.DATE)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return ResponseEntity.ok(reportService.getReportByDate(date));
    }

    @Cacheable("testCache")
    @GetMapping(value = "/reports/date/range", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByDateRange(@RequestParam(value = Parameters.START_DATE)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                          @RequestParam(value = Parameters.END_DATE)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ResponseEntity.ok(reportService.getReportByDateRange(startDate, endDate));
    }

    @Cacheable("testCache")
    @GetMapping(value = "/reports/asin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByAsin() {
        return ResponseEntity.ok(reportService.getAllReportByAsin());
    }

    @Cacheable("testCache")
    @GetMapping(value = "/reports/asin/{asin}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByAsin(@PathVariable(value = Parameters.ASIN) String ASIN) {
        return ResponseEntity.ok(reportService.getReportByAsin(ASIN));
    }

    @Cacheable("testCache")
    @GetMapping(value = "/reports/asin/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByAsinList(@RequestParam(value = Parameters.ASIN) List<String> asinList) {
        return ResponseEntity.ok(reportService.getReportByAsinList(asinList));
    }

    @Scheduled(cron = "0 0/5 * * * *")
    @CacheEvict(value = "testCache", allEntries = true)
    public void evictAllCacheValues() {
        cacheManager.getCache("testCache").clear();
    }
}
