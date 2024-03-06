package com.neo.mongocachetest.dto.response;

import lombok.Data;


import java.util.List;

@Data
public class ReportDTO {

    public ReportSpecificationDTO reportSpecification;

    public List<SalesAndTrafficByDateDTO> salesAndTrafficByDate;

    public List<SalesAndTrafficByAsinDTO> salesAndTrafficByAsin;
}
