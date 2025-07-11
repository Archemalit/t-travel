package ru.tbank.itis.tripbackend.handler;

import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.dto.response.ValidationErrorResponse;
import ru.tbank.itis.tripbackend.dto.response.ValidationErrorResponse.ValidationError;
import ru.tbank.itis.tripbackend.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMissingParams(MissingServletRequestParameterException ex) {
        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Отсутствует обязательный параметр",
                List.of(new ValidationError(
                        ex.getParameterName(),
                        null,
                        "Параметр '" + ex.getParameterName() + "' обязателен"
                ))
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = "object";
                    Object rejectedValue = null;

                    if (error instanceof FieldError fieldError) {
                        field = fieldError.getField();
                        rejectedValue = fieldError.getRejectedValue();
                    }

                    return new ValidationError(field, rejectedValue, error.getDefaultMessage());
                })
                .collect(Collectors.toList());

        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации данных",
                errors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationError> errors = ex.getConstraintViolations().stream()
                .map(violation -> new ValidationError(
                        violation.getPropertyPath().toString().split("\\.")[1],
                        violation.getInvalidValue(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());

        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации данных",
                errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка парсинга JSON",
                "Неверный формат JSON: " + ex.getCause().getMessage()
        );
    }

    @ExceptionHandler(TripNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handleTripNotFound(TripNotFoundException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Поездка не найдена",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public SimpleErrorResponse handleForbiddenAccess(ForbiddenAccessException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Доступ запрещен",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleErrorResponse handleValidationException(ValidationException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ParticipantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handleParticipantNotFound(ParticipantNotFoundException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Участник не найден",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvitationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handleInvitationNotFound(InvitationNotFoundException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Приглашение не найдено",
                ex.getMessage()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Пользователь не найден",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ExpiredInvitationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handleExpiredInvitation(ExpiredInvitationException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Приглашение не активно",
                ex.getMessage()
        );
    }

    @ExceptionHandler(PhoneNumberAlreadyTakenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationException(PhoneNumberAlreadyTakenException ex) {
        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации данных",
                List.of(new ValidationError(ex.getField(), ex.getRejectedValue(), ex.getMessage()))
        );
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handleExpenseNotFound(ExpenseNotFoundException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Расход не найден",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ExpenseForMySelfException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleErrorResponse handleExpenseForMySelf(ExpenseForMySelfException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Расход на самого себя невозможен",
                ex.getMessage()
        );
    }

    @ExceptionHandler(SeveralExpensesForUser.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handleSeveralExpensesForUser(SeveralExpensesForUser ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка распределения расходов ",
                ex.getMessage()
        );
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handleNotificationNotFound(NotificationNotFoundException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Уведомление не найдено",
                ex.getMessage()
        );
    }

    @ExceptionHandler(PlannedExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse handlePlannedExpenseNotFound(PlannedExpenseNotFoundException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Запланированный расход не найден",
                ex.getMessage()
        );
    }
    @ExceptionHandler(FirebaseMessagingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleErrorResponse handleFirebaseMessagingException(FirebaseMessagingException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ошибка отправки push-уведомления",
                ex.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleErrorResponse handleRuntimeException(RuntimeException ex) {
        if (ex.getCause() instanceof FirebaseMessagingException) {
            return handleFirebaseMessagingException((FirebaseMessagingException) ex.getCause());
        }
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Внутренняя ошибка сервера",
                ex.getMessage()
        );
    }

    @ExceptionHandler(PlannedExpenseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleErrorResponse handlePlannedExpense(PlannedExpenseException ex) {
        return new SimpleErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка запланированного расхода",
                ex.getMessage()
        );
    }


//    @ExceptionHandler(InvalidRefreshTokenException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public SimpleErrorResponse handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
//        return new SimpleErrorResponse(
//                LocalDateTime.now(),
//                HttpStatus.UNAUTHORIZED.value(),
//                "Неверный refresh-токен",
//                ex.getMessage()
//        );
//    }

}