package com.onclass.capacity.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    MISSING_REQUIRED_PARAM("400", "Missing required parameters, please verify data ", ""),
    INVALID_MESSAGE_ID("404", "Invalid Message ID, please verify", "messageId"),
    CAPACITY_NAME_TOO_LONG("400", "The capacity name is too long", ""),
    CAPACITY_DESCRIPTION_TOO_LONG("400", "The capacity description is too long", ""),
    UNSUPPORTED_OPERATION("501", "Method not supported, please try again", ""),
    CAPACITY_CREATED("201", "capacity created successful", ""),
    CAPACITY_ALREADY_EXISTS("400","The capacity already exist." ,"" ),
    TECHNOLOGIES_NOT_FOUND("404","Some of the technologies to be register were not found, please verify data" ,"" ),
    ERROR_ASSIGN_TECHNOLOGIES("500","Something went wrong with the capacity adapter, please try again" ,"" ),
    LIST_TECHNOLOGIES_IS_TOO_SHORT("404","List of the technologies must contain at least one" ,"" ),
    LIST_TECHNOLOGIES_IS_TOO_LONG("404","List of the technologies exceeds the allowed limit" ,"" ),
    ERROR_CREATING_CAPACITY("500","An error occurred while creating the capacity" ,"" );

    private final String code;
    private final String message;
    private final String param;
}