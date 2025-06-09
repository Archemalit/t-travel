package ru.tbank.itis.tripbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.PlannedExpenseDto;
import ru.tbank.itis.tripbackend.dto.request.PlannedExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.PlannedExpenseResponse;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.dto.response.ValidationErrorResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.PlannedExpenseService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plan/expenses")
@RequiredArgsConstructor
public class PlannedExpenseController {

    private final PlannedExpenseService plannedExpenseService;

//    @GetMapping("/{expenseId}")
//    public PlannedExpenseResponse getExpenseById(@PathVariable Long tripId, @PathVariable Long expenseId,
//                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return plannedExpenseService.getExpenseById(tripId, userDetails.getId(), expenseId);
//    }

    @GetMapping("/{tripId}")
    @Operation(
            summary = "Получение всех запланированных трат поездки",
            description = "Возвращает список всех запланированных трат, если пользователь является участником или создателем поездки",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список трат успешно загружен",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlannedExpenseResponse.class))
                            )),
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
                                            description = "Недостаточно прав для получения информации о запланированных расходах"
                                    )
                            })
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Поездка не найдена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public List<PlannedExpenseResponse> getAllExpensesByTripId(@PathVariable Long tripId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return plannedExpenseService.getAllExpensesByTripId(tripId, userDetails.getId());
    }

    @PostMapping("/{tripId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создание новой запланированной траты",
            description = "Создаёт новую запланированную трату, если пользователь — участник или создатель поездки",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Запланированная трата успешно создана",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlannedExpenseResponse.class)
                            )),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
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
                                            description = "Недостаточно прав для создания запланированного расхода"
                                    )
                            })
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Поездка не найдена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public PlannedExpenseResponse createExpense(@PathVariable Long tripId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @Valid @RequestBody PlannedExpenseRequest expenseDto) {
        return plannedExpenseService.createExpense(tripId, userDetails.getId(), expenseDto);
    }

//    @PutMapping("/{expenseId}")
//    public PlannedExpenseDto updateExpense(@PathVariable Long tripId, @PathVariable Long expenseId,
//                                           @AuthenticationPrincipal UserDetailsImpl userDetails,
//                                           @Valid @RequestBody PlannedExpenseRequest expenseDto) {
//        return plannedExpenseService.updateExpense(tripId, userDetails.getId(), expenseId, expenseDto);
//    }

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление запланированной траты",
            description = "Удаляет запланированную трату, если пользователь — её автор или создатель поездки",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Запланированная трата удалена"),
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
                                            description = "Недостаточно прав для удаления запланированного расхода"
                                    )
                            })
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Запланированная трата не найдена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public void deleteExpense(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @PathVariable Long expenseId) {
        plannedExpenseService.deleteExpense(userDetails.getId(), expenseId);
    }
}
