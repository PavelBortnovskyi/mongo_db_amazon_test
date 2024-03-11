package com.neo.mongocachetest.dto.response;

import com.neo.mongocachetest.enums.Granularity;
import lombok.Data;

@Data
public class ReportOptionsDTO {

    public Granularity dateGranularity;

    public String asinGranularity;
}
