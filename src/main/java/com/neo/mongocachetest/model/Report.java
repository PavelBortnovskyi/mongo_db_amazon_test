package com.neo.mongocachetest.model;

import com.neo.mongocachetest.annotation.CascadeSave;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Report extends BaseDocument {

    @DBRef
    @CascadeSave
    public ReportSpecification reportSpecification;

    @DBRef
    @CascadeSave
    public List<SalesAndTrafficByDate> salesAndTrafficByDate;

    @DBRef
    @CascadeSave
    public List<SalesAndTrafficByAsin> salesAndTrafficByAsin;
}
