package org.example.currencyserver.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Provides custom exception handling for the responses to the clients requests.
 */
@ControllerAdvice
@RestController
public class CustomizedExceptionHandling extends ResponseEntityExceptionHandler {

    /**
     * Handles the particular errors thrown from the interaction with the SWOP server
     * and delivers them as specific error response.
     *
     * @param ex the thrown exception.
     * @return the response as entity.
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public final ResponseEntity<Object> handleRestClientException(HttpClientErrorException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
    }

    /**
     * Handles the highest level of rest client exceptions,
     * generally thrown from this application and to wrap them as an Internal Server Error.
     *
     * @param ex the thrown exception.
     * @return the response as entity.
     */
    @ExceptionHandler(RestClientException.class)
    public final ResponseEntity<Object> handleRestClientException(RestClientException ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

}
