package com.onclass.capacity.domain.exceptions;

import com.onclass.capacity.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class ParamRequiredMissingException extends BusinessException {

    public ParamRequiredMissingException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }


}
