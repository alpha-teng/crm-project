package com.crm.dto.request;

import lombok.Data;

@Data
public class SearchRequest {
    private String query;
    private Integer limit = 5;
}
