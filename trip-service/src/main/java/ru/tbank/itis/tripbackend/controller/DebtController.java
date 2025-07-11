package ru.tbank.itis.tripbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.itis.tripbackend.dto.DebtDto;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.ActualExpenseService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/debts")
@RequiredArgsConstructor
@Tag(name = "Debts", description = "API для работы с долгами поездки")
public class DebtController {
    private final ActualExpenseService actualExpenseService;

    @GetMapping("/{tripId}")
    @Operation(
            summary = "Получение всех долгов поездки",
            description = "Возвращает список всех долгов поездки, если пользователь является участником или создателем",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список долгов успешно загружен",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DebtDto.class)))
                    ),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class), examples = {
                                    @ExampleObject(
                                            name = "Example",
                                            value = """
                                                    {
                                                      "timestamp": "2025-06-08T12:00:00Z",
                                                      "status": 401,
                                                      "error": "Unauthorized",
                                                      "message": "Вы не передали access-токен!"
                                                    }
                                                    """,
                                            description = "Не был передан access-токен в заголовок"
                                    )
                            })
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Пользователь не имеет доступа к этой поездке",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class), examples = {
                                    @ExampleObject(
                                            name = "Example",
                                            value = """
                                                    {
                                                      "timestamp": "2025-05-24T14:34:43.268036",
                                                      "status": 403,
                                                      "error": "Доступ запрещен",
                                                      "message": "Доступа нет!"
                                                    }
                                                    """,
                                            description = "Недостаточно прав для получения информации о долгах поездки"
                                    )
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "Поездка найдена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
            }
    )
    public List<DebtDto> getAllDebtsByTrip(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return actualExpenseService.getAllDebtsByTrip(tripId, userDetails.getId());
    }
}
