package ru.practicum.shareit.handler.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BookingAccessException;
import ru.practicum.shareit.booking.exception.BookingByOwnerOfItemException;
import ru.practicum.shareit.booking.exception.BookingUpdateException;
import ru.practicum.shareit.booking.exception.IncorrectTimeOfBookingException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.item.exception.CommentByNotBookerException;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.UpdateByNotOwnerException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFailValidation(final MethodArgumentNotValidException e) {
        log.error("Получен статус 400(BAD REQUEST), сообщение {}", e.getMessage());
        log.error("Stack-trace ошибки: {}", e.getStackTrace().toString());
        StringBuilder errorMessage = new StringBuilder();
        for (int i = 0; i < e.getBindingResult().getFieldErrorCount(); i++) {
            errorMessage.append(e.getBindingResult().getFieldErrors().get(i).getField() + " ");
            errorMessage.append(e.getBindingResult().getFieldErrors().get(i).getDefaultMessage() + ";");
        }
        return new ErrorResponse(errorMessage.toString());
    }

    @ExceptionHandler({
            ItemNotAvailableException.class,
            IncorrectTimeOfBookingException.class,
            BookingUpdateException.class,
            CommentByNotBookerException.class,
            InvalidParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemNotAvailable(final RuntimeException e) {
        log.error("Получен статус 400(BAD REQUEST), сообщение {}", e.getMessage());
        log.error("Stack-trace ошибки: {}", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({BookingAccessException.class, EntityNotExistException.class, BookingByOwnerOfItemException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        log.error("Получен статус 404(NOT FOUND), сообщение {}", e.getMessage());
        log.error("Stack-trace ошибки: {}", e.getStackTrace().toString());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(final UpdateByNotOwnerException e) {
        log.error("Получен статус 403(FORBIDDEN), сообщение {}", e.getMessage());
        log.error("Stack-trace ошибки: {}", e.getStackTrace().toString());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleConstraintError(final DataIntegrityViolationException e) {
        log.error("Получен статус 500(INTERNAL_SERVER_ERROR), сообщение {}", e.getMessage());
        log.error("Stack-trace ошибки: {}", e.getStackTrace().toString());
        String message = e.getCause().getCause().getMessage();
        return new ErrorResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleError(final Exception e) {
        log.error("Получен статус 500(INTERNAL_SERVER_ERROR), сообщение {}", e.getMessage());
        log.error("Stack-trace ошибки: {}", e.getCause());
        return new ErrorResponse(e.getMessage());
    }

}
