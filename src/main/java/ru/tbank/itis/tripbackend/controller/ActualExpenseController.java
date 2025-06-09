package ru.tbank.itis.tripbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.DebtDto;
import ru.tbank.itis.tripbackend.dto.request.ExpenseRequest;
import ru.tbank.itis.tripbackend.dto.response.ExpenseResponse;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.dto.response.ValidationErrorResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.ActualExpenseService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Expenses", description = "API для управления расходами поездок")
public class ActualExpenseController {

    private final ActualExpenseService actualExpenseService;

    @GetMapping("/{expenseId}")
    @Operation(
            summary = "Получение информации о расходе",
            description = "Возвращает расход по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Расход успешно загружен",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpenseResponse.class))),
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
                                            description = "Недостаточно прав для получения информации о расходе"
                                    )
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "Расход не найден",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
            }
    )
    public ExpenseResponse getExpenseById(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable Long expenseId) {
        return actualExpenseService.getExpenseById(userDetails.getId(), expenseId);
    }

    @GetMapping("/trip/{tripId}")
    @Operation(
            summary = "Получение всех расходов поездки",
            description = "Возвращает список всех расходов, связанных с указанной поездкой",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список расходов успешно загружен",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExpenseResponse.class)))),
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
                                            description = "Недостаточно прав для получения информации о расходах поездки"
                                    )
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "Поездка найдена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
            }
    )
    public List<ExpenseResponse> getAllExpensesByTrip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable Long tripId) {
        return actualExpenseService.getAllExpensesByTrip(userDetails.getId(), tripId);
    }

//    @GetMapping("/{tripId}/member/{memberId}")
//    @Operation(
//            summary = "Получение всех расходов участника поездки",
//            description = "Возвращает список всех расходов, за которые платил участник поездки",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Список расходов успешно загружен",
//                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExpenseParticipantResponse.class)))),
//                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
//                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class), examples = {
//                                    @ExampleObject(
//                                            name = "Example",
//                                            value = """
//                                                    {
//                                                      "timestamp": "2025-06-08T12:00:00Z",
//                                                      "status": 401,
//                                                      "error": "Unauthorized",
//                                                      "message": "Вы не передали access-токен!"
//                                                    }
//                                                    """,
//                                            description = "Не был передан access-токен в заголовок"
//                                    )
//                            })
//                    ),
//                    @ApiResponse(
//                            responseCode = "403",
//                            description = "Пользователь не имеет доступа к этой поездке",
//                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class), examples = {
//                                    @ExampleObject(
//                                            name = "Example",
//                                            value = """
//                                                    {
//                                                      "timestamp": "2025-05-24T14:34:43.268036",
//                                                      "status": 403,
//                                                      "error": "Доступ запрещен",
//                                                      "message": "Доступа нет!"
//                                                    }
//                                                    """,
//                                            description = "Недостаточно прав для получения информации о расходах участника"
//                                    )
//                            })
//                    ),
//                    @ApiResponse(responseCode = "404", description = "Поездка или участник не найдены",
//                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
//            }
//    )
//    public List<ExpenseParticipantResponse> getAllExpensesByTripMember(@PathVariable Long tripId, @PathVariable Long memberId) {
//        return actualExpenseService.getAllExpensesByTripAndMember(tripId, memberId);
//    }

    @PostMapping("/{tripId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создание нового расхода",
            description = "Создаёт новый расход и связывает его с поездкой",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Расход успешно создан",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpenseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных запроса",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
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
                                            description = "Недостаточно прав для создания расхода"
                                    )
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "Поездка не найдена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
            }
    )
    public ExpenseResponse createExpense(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable Long tripId, @Valid @RequestBody ExpenseRequest expenseDto) {
        return actualExpenseService.createExpense(userDetails.getUser(), tripId, expenseDto);
    }

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление расхода",
            description = "Удаляет указанный расход, если пользователь — его создатель",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Расход успешно удален"),
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
                                            description = "Недостаточно прав для удаления расхода"
                                    )
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "Расход не найден",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
            }
    )


    public void deleteExpense(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @PathVariable Long expenseId) {
        actualExpenseService.deleteExpense(userDetails.getId(), expenseId);
    }

}
