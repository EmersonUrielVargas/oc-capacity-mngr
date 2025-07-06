package com.onclass.capacity.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum OrderList {

    ASCENDANT("ASC"),
    DESCENDANT("DESC");

    private final String message;

    public static OrderList fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("El valor no puede ser nulo.");
        }
        return Arrays.stream(OrderList.values())
                     .filter(item -> item.getMessage().equalsIgnoreCase(value))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Valor desconocido: " + value));
    }
}