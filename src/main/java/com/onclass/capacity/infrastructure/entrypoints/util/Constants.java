package com.onclass.capacity.infrastructure.entrypoints.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String CAPACITY_ERROR = "Error on capacity - [ERROR]";
    public static final String CAPACITY_CREATED_RS_OK = "capacity created successfully";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";

    public final String QUERY_PARAM_ORDER_SORT = "sort";
    public final String QUERY_PARAM_ITEM_SORT = "parameter";
    public final String QUERY_PARAM_PAGE = "page";
    public final String QUERY_PARAM_SIZE = "size";
    public final String DEFAULT_SIZE_PAGINATION = "10";
    public final String DEFAULT_PAGE_PAGINATION = "0";
}
