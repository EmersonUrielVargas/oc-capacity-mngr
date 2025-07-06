package com.onclass.capacity.domain.exceptions;

import com.onclass.capacity.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends ProcessorException {

    public TechnicalException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause, technicalMessage);
    }

    public TechnicalException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }
}