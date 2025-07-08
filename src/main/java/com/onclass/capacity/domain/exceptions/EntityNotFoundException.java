package com.onclass.capacity.domain.exceptions;

import com.onclass.capacity.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }


}
