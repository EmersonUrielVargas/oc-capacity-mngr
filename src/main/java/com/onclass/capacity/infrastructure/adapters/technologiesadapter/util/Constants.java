package com.onclass.capacity.infrastructure.adapters.technologiesadapter.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public final String NO_ADITIONAL_ERROR_DETAILS = "No additional error details";
    public final String LOG_START_ASSIGN_TECHNOLOGIES = "Starting assign technologies {} in a capacity id: {}";
    public final String LOG_START_GET_TECHNOLOGIES_PAGINATION = "Starting get technologies with parameters order: {}, page: {}, size: {}";
    public final String LOG_START_GET_TECHNOLOGIES_BY_CAPABILITIES_IDS = "Starting get technologies with capabilities ids : {}";

    public final String TECHNOLOGY_MNGR_PATH_ASSIGN = "/assing";
    public final String TECHNOLOGY_MNGR_PATH_GET_BY_CAPABILITIES = "/technologies";

    public final String QUERY_PARAM_ORDER_SORT = "sort";
    public final String QUERY_PARAM_PAGE = "page";
    public final String QUERY_PARAM_SIZE = "size";

    public final String STRING_ERROR_BODY_DATA = "Error body: {}";
}
