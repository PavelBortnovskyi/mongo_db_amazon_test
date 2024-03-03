package com.neo.mongocachetest.dto;

import com.neo.mongocachetest.model.BaseDocument;
import com.neo.mongocachetest.model.ReportSpecification;
import com.neo.mongocachetest.model.SalesAndTrafficByAsin;
import com.neo.mongocachetest.model.SalesAndTrafficByDate;
import lombok.Data;


import java.util.List;

@Data
public class ReportDTO extends BaseDocument {

    public ReportSpecification reportSpecification;

    public List<SalesAndTrafficByDate> salesAndTrafficByDate;

    public List<SalesAndTrafficByAsin> salesAndTrafficByAsin;
}
