package com.iyzico.challenge.integrator.exception.auth;

import com.iyzico.challenge.integrator.dto.ErrorCode;
import com.iyzico.challenge.integrator.exception.BaseIntegratorException;

public class InvalidCredentialsException extends BaseIntegratorException {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.INVALID_CREDENTIALS;
    }
}
