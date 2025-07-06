package com.onclass.capacity.domain.exceptions;

import com.onclass.capacity.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class InvalidFormatParamException extends BusinessException {

    public InvalidFormatParamException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }


}
