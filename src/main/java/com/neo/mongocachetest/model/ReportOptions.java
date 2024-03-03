package com.neo.mongocachetest.model;

import com.neo.mongocachetest.enums.Granularity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ReportOptions extends BaseDocument{

    public Granularity dateGranularity;

    public String asinGranularity;
}
