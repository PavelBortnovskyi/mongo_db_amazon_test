package com.neo.mongocachetest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neo.mongocachetest.enums.ReportType;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document
public class ReportSpecification extends BaseDocument{

    public ReportType reportType;

    @DBRef
    public ReportOptions reportOptions;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    public Date dataStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    public Date dataEndTime;

    public List<String> marketplaceIds;
}
