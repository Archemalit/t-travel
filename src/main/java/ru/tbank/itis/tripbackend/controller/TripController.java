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
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.TripRequest;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.dto.response.TripResponse;
import ru.tbank.itis.tripbackend.dto.response.UserExistsResponse;
import ru.tbank.itis.tripbackend.dto.response.ValidationErrorResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.TripService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "API для управления поездками")
public class TripController {

    private final TripService tripService;

    @GetMapping
    @Operation(
            summary = "Получение всех поездок пользователя",
            description = "Возвращает список поездок, где пользователь является участником или создателем",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список поездок успешно загружен",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripResponse.class)))),
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
                            })),
                    @ApiResponse(responseCode = "404", description = "Поездки не найдены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
            }
    )
    public List<TripResponse> getAllTrips(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestParam(name = "onlyCreator", required = false, defaultValue = "false") boolean onlyCreator) {
        return tripService.getAllTripsByUserId(userDetails.getId(), onlyCreator);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение деталей поездки",
            description = "Возвращает данные конкретной поездки, если пользователь её участник или создатель",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Детали поездки успешно получены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TripResponse.class))
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
                                            description = "Недостаточно прав для получения информации о поездке"
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
    public TripResponse getTripById(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @PathVariable("id") Long id) {
        return tripService.getTripById(id, userDetails.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создание новой поездки",
            description = "Создаёт новую поездку, устанавливает текущего пользователя как создателя",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Поездка успешно создана",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TripResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации данных запроса",
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
                    )
            }
    )
    public TripResponse createTrip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @Valid @RequestBody TripRequest tripRequest) {
        return tripService.createTrip(tripRequest, userDetails.getUser());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновление поездки",
            description = "Обновляет данные поездки, если пользователь — её создатель",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Поездка успешно обновлена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TripResponse.class))
                    ),
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
                                            description = "Недостаточно прав для обновления данных о поездке"
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
    public TripResponse updateTrip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long id, @Valid @RequestBody TripRequest tripRequest) {
        return tripService.updateTrip(id, tripRequest, userDetails.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление поездки",
            description = "Удаляет поездку, если пользователь — её создатель",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Поездка успешно удалена"),
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
                                            description = "Недостаточно прав для удаления поездки"
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
    public void deleteTrip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @PathVariable Long id) {
        tripService.deleteTrip(id, userDetails.getId());
    }

    @PatchMapping("/{id}/archive")
    @Operation(
            summary = "Архивирование поездки",
            description = "Переводит поездку в архивный статус, если пользователь — её создатель",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Поездка успешно переведена в архив"
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
                                            description = "Недостаточно прав для архивирования поездки"
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
    public void archiveTrip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @PathVariable Long id) {
        tripService.archiveTrip(id, userDetails.getId());
    }
}