package com.neo.mongocachetest.controller;

import com.neo.mongocachetest.constants.Parameters;
import com.neo.mongocachetest.dto.ReportDTO;
import com.neo.mongocachetest.model.Report;
import com.neo.mongocachetest.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Log4j2
@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByDate() {
        return ResponseEntity.ok(reportService.getTotalReport());
    }

    @GetMapping(value = "/reports/date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByDate(@PathVariable(value = Parameters.DATE)
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return ResponseEntity.ok(reportService.getReportByDate(date));
    }

    @GetMapping(value = "/reports/date/range", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDTO> getReportByDateRange(@RequestParam(value = Parameters.START_DATE)
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                     @RequestParam(value = Parameters.END_DATE)
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ResponseEntity.ok(reportService.getReportByDateRange(startDate, endDate));
    }
//
//    @GetMapping(value = "/reports/asin/{asin}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ReportDTO> getReportByAsin(@PathVariable(value = Parameters.ASIN) String asin) {
//
//    }
//
//    @GetMapping(value = "/reports/asin/list", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ReportDTO> getReportByAsinList(@RequestParam(value = Parameters.ASIN) List<String> asinList) {
//
//    }
}
