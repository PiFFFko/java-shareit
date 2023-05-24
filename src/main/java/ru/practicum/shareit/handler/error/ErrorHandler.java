package ru.practicum.shareit.handler.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.item.exception.UpdateByNotOwnerException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExist(final EntityAlreadyExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFailValidation(final MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        for (int i = 0; i < e.getBindingResult().getFieldErrorCount(); i++) {
            errorMessage.append(e.getBindingResult().getFieldErrors().get(i).getField() + " ");
            errorMessage.append(e.getBindingResult().getFieldErrors().get(i).getDefaultMessage() + ";");
        }
        return new ErrorResponse(errorMessage.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final EntityNotExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(final UpdateByNotOwnerException e) {
        return new ErrorResponse(e.getMessage());
    }

}
