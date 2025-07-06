package com.onclass.capacity.domain.exceptions;

import com.onclass.capacity.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class EntityAlreadyExistException extends BusinessException {

    public EntityAlreadyExistException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }


}
