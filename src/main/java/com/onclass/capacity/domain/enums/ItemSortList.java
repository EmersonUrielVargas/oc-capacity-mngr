package com.onclass.capacity.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ItemSortList {

    NAME("name"),
    CAPABILITIES("technologies");

    private final String message;

    public static ItemSortList fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("El valor no puede ser nulo.");
        }
        return Arrays.stream(ItemSortList.values())
                     .filter(item -> item.getMessage().equalsIgnoreCase(value))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Valor desconocido: " + value));
    }
}