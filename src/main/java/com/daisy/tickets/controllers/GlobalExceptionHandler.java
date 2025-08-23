package com.daisy.tickets.controllers;


import com.daisy.tickets.domain.dtos.ErrorDTO;
import com.daisy.tickets.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleTicketNotFoundException(TicketNotFoundException ex) {
        log.error("Caught ticket not found exception", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("Ticket not found");

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TicketSoldOutException.class)
    public ResponseEntity<ErrorDTO> handleTicketSoldOutException(TicketSoldOutException ex) {
        log.error("Caught ticket sold out exception", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("An error occurred when generating QR Code (the ticket has been sold out).");

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QRCodeNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleQRCodeNotFound(QRCodeNotFoundException ex) {
        log.error("Caught QR Code Not Found exception", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("The QR Code was not found.");

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(QRCodeGenerationException.class)
    public ResponseEntity<ErrorDTO> handleQRCodeGenerationException(QRCodeGenerationException ex) {
        log.error("Caught QRCode generation exception", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("An error occurred when generating QR Code.");

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EventUpdateException.class)
    public ResponseEntity<ErrorDTO> handleEventUpdateException(EventUpdateException ex) {
        log.error("Caught EventUpdateException", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("An error occurred when updating event.");

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TicketTypeNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleTicketTypeNotFoundException(TicketTypeNotFoundException ex) {
        log.error("Caught TicketTypeNotFoundException", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("Ticket type not found");

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleEventNotFoundException(EventNotFoundException ex) {
        log.error("Caught EventNotFoundException", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("Event not found");

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("Caught UserNotFoundException", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("User not found");

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Caught MethodArgumentNotValidException", ex);
        ErrorDTO errorDTO = new ErrorDTO();

        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String errMessage = fieldErrors
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .orElse("Validation error occurred.");

        errorDTO.setError(errMessage);

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Caught ConstraintViolationException", ex);
        ErrorDTO errorDTO = new ErrorDTO();

        String errMessage = ex.getConstraintViolations()
                .stream()
                        .findFirst()
                                .map(violation ->
                                    violation.getPropertyPath() + ": " + violation.getMessage()
                                ).orElse("Constrain violation occurred.");

        errorDTO.setError(errMessage);

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception ex) {
        log.error("Caught exception", ex);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setError("An unknown error occurred");

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
