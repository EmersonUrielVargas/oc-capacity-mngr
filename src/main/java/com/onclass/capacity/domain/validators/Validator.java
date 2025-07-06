package com.onclass.capacity.domain.validators;

import com.onclass.capacity.domain.constants.Constants;
import com.onclass.capacity.domain.enums.TechnicalMessage;
import com.onclass.capacity.domain.exceptions.InvalidFormatParamException;
import com.onclass.capacity.domain.exceptions.ParamRequiredMissingException;
import com.onclass.capacity.domain.model.Capacity;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class Validator {

	public static Mono<Capacity> validateCapacity(Capacity capacity) {
        return Mono.just(capacity)
                .then(validateName(capacity))
                .then(validateDescription(capacity))
                .then(validateListTechnologies(capacity));
    }

    private static Mono<Capacity> validateListTechnologies(Capacity capacity) {
        if (isNullOrEmpty(capacity.technologies())){
            return Mono.error(new ParamRequiredMissingException(TechnicalMessage.MISSING_REQUIRED_PARAM));
        }
        if (capacity.technologies().size() < Constants.CAPACITY_MIN_TECHNOLOGIES_SIZE) {
            return Mono.error(new InvalidFormatParamException(TechnicalMessage.LIST_TECHNOLOGIES_IS_TOO_SHORT));
        }
        if (capacity.technologies().size() > Constants.CAPACITY_MAX_CAPABILITIES_SIZE) {
            return Mono.error(new InvalidFormatParamException(TechnicalMessage.LIST_TECHNOLOGIES_IS_TOO_LONG));
        }
        return Mono.just(capacity);
    }

    private static Mono<Capacity> validateName(Capacity capacity) {
        if (isNullOrEmpty(capacity.name())){
            return Mono.error(new ParamRequiredMissingException(TechnicalMessage.MISSING_REQUIRED_PARAM));
        }
        if (capacity.name().length()  > Constants.CAPACITY_NAME_MAX_SIZE) {
            return Mono.error(new InvalidFormatParamException(TechnicalMessage.CAPACITY_NAME_TOO_LONG));
        }
        return Mono.just(capacity);
    }

    private static Mono<Capacity> validateDescription(Capacity capacity) {
        if (isNullOrEmpty(capacity.description())){
            return Mono.error(new ParamRequiredMissingException(TechnicalMessage.MISSING_REQUIRED_PARAM));
        }
        if (capacity.description().length()  > Constants.CAPACITY_DESCRIPTION_MAX_SIZE) {
            return Mono.error(new InvalidFormatParamException(TechnicalMessage.CAPACITY_DESCRIPTION_TOO_LONG));
        }
        return Mono.just(capacity);
    }

    public static <T> boolean isNullOrEmpty(T value){
        if (Objects.nonNull(value)){
            if (value instanceof String str) {
                return str.isBlank();
            }else {
                return false;
            }
        }
        return true;
	}
}
