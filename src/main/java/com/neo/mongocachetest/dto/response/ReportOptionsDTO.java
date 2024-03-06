package com.neo.mongocachetest.dto.response;

import com.neo.mongocachetest.enums.Granularity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class ReportOptionsDTO {

    public Granularity dateGranularity;

    public String asinGranularity;
}
