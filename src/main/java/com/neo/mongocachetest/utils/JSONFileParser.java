package com.neo.mongocachetest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.mongocachetest.model.Report;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Log4j2
@Service
@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "dbfile")
public class JSONFileParser {

    private String name;

    private String path;

    private final ObjectMapper objectMapper;

    public Optional<Report> extractReportsFromFile () {
        try {
            return Optional.of(objectMapper.readValue(new File(path + name), Report.class));
        } catch (IOException e) {
            log.error("Got error while read data from file: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
