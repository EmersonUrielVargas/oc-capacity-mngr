package com.onclass.capacity.infrastructure.entrypoints.util;

import com.onclass.capacity.domain.model.spi.CapabilitiesPerBootcamp;
import com.onclass.capacity.domain.model.spi.CapacityList;
import com.onclass.capacity.domain.utilities.CustomPage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String CAPACITY_ERROR = "Error on capacity - [ERROR] {}";
    public static final String CAPACITY_CREATED_RS_OK = "capacity created successfully";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";
    public static final String ASSIGN_CAPABILITIES_CREATED_RS_OK = "Capabilities assign successfully";
    public static final String GET_BOOTCAMPS_CAPABILITIES_RS_OK = "Get bootcamp list with capabilities successfully";

    public static final String PATH_POST_CAPABILITY = "/capacity";
    public static final String PATH_GET_ALL_CAPABILITIES = "/capacity/all";
    public static final String PATH_POST_ASSIGN_CAPABILITIES = "/capacity/assign";
    public static final String PATH_GET_CAPABILITIES_BY_BOOTCAMPS_IDS = "/capacity/bootcamps_ids";
    public static final String PATH_GET_CAPABILITIES_SORT_BY_BOOTCAMPS = "/capacity/bootcamps";
    public static final String PATH_DELETE_CAPABILITIES_BY_BOOTCAMP = "/capacity/bootcamp/{id}";


    public final String QUERY_PARAM_ORDER_SORT = "sort";
    public final String QUERY_PARAM_CAPABILITIES_IDS = "capabilitiesIds";
    public final String QUERY_PARAM_ITEM_SORT = "parameter";
    public final String QUERY_PARAM_PAGE = "page";
    public final String QUERY_PARAM_SIZE = "size";
    public final String QUERY_PARAM_ID = "id";
    public final String DEFAULT_SIZE_PAGINATION = "10";
    public final String DEFAULT_PAGE_PAGINATION = "0";
    
    
    public final String MESSAGE_UNAUTHORIZED = "Unauthorized";
    public final String MESSAGE_VALIDATION_ERROR = "Validation error";
    
    /*Class to Open API*/
    public class CustomPageCapacityList extends CustomPage<CapacityList> {}
    public class CustomPageCapabilitiesPerBootcamp extends CustomPage<CapabilitiesPerBootcamp> {}
}
