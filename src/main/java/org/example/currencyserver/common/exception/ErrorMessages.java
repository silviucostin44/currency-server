package org.example.currencyserver.common.exception;

public class ErrorMessages {

    public static final String CLIENT_ERROR_MESSAGE_403 =
            "(ERR0403) Request to SWOP for simple rate failed with 403 error status. "
                    + "Please check the format of the currencies or the ApiKey. "
                    + "Otherwise the request is not supported by the backing service";

    private ErrorMessages() {
    }
}
