package com.neo.mongocachetest.model;

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

    public Date dataStartTime;

    public Date dataEndTime;

    public List<String> marketplaceIds;
}
