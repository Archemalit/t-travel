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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.*;
import ru.tbank.itis.tripbackend.dto.common.SimpleResponse;
import ru.tbank.itis.tripbackend.dto.request.InviteRequest;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.dto.response.ValidationErrorResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/members")
@RequiredArgsConstructor
@Tag(name = "Trip Members", description = "API для управления участниками поездки")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Приглашение пользователя в поездку",
            description = "Создаёт приглашение и запись о потенциальном участнике. Только создатель поездки может приглашать пользователей.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь успешно приглашён"),
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
                                            description = "Недостаточно прав для добавления участников в поездку"
                                    )
                            })
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Поездка или пользователь не найдены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public void inviteMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody InviteRequest inviteRequest
    ) {
        memberService.inviteMember(tripId, userDetails.getUser(), inviteRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление участника из поездки",
            description = "Удаляет участника из поездки. Только создатель поездки может удалять участников.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Участник успешно удален"),
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
                                            description = "Недостаточно прав для удаления участников из поездки"
                                    )
                            })
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Поездка или пользователь не найдены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public void removeMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long tripId,
            @PathVariable Long userId
    ) {
        memberService.removeMember(tripId, userId, userDetails.getUser());
    }

    @GetMapping
    @Operation(
            summary = "Получение списка участников поездки",
            description = "Возвращает список активных участников поездки. Пользователь должен быть участником или создателем поездки.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список участников успешно загружен",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripParticipantDto.class)))),
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
                                            description = "Недостаточно прав для получения участников поездки"
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
    public List<TripParticipantDto> getActiveMembers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long tripId
    ) {
        return memberService.getActiveMembers(tripId, userDetails.getUser());
    }
}