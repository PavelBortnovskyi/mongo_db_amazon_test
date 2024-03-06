package com.neo.mongocachetest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neo.mongocachetest.enums.ReportType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReportSpecificationDTO{

    public ReportType reportType;

    public ReportOptionsDTO reportOptions;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    public Date dataStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    public Date dataEndTime;

    public List<String> marketplaceIds;
}
