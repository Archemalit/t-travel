package ru.tbank.itis.tripbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.TripInvitationDto;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.InvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "API для работы с приглашениями в поездку")
public class InvitationController {
    private final InvitationService invitationService;

    @GetMapping
    @Operation(
            summary = "Получение активных приглашений пользователя",
            description = "Возвращает список всех активных приглашений, где пользователь является приглашённым",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список приглашений успешно загружен",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TripInvitationDto.class)))),
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
    public List<TripInvitationDto> getUserInvitations(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return invitationService.getUserInvitations(userDetails.getId());
    }

    @PostMapping("/{invitationId}/accept")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Принятие приглашения",
            description = "Позволяет пользователю принять приглашение в поездку",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Приглашение успешно принято"),
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
                            description = "Пользователь не имеет доступа к этому приглашению",
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
                                            description = "Недостаточно прав для принятия этого приглашения"
                                    )
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "Приглашение или участник не найдены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
            }
    )
    public void acceptInvitation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long invitationId
    ) {
        invitationService.acceptInvitation(invitationId, userDetails.getId());
    }

    @PostMapping("/{invitationId}/reject")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Отклонение приглашения",
            description = "Позволяет пользователю отклонить приглашение в поездку",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Приглашение успешно отклонено"),
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
                            description = "Пользователь не имеет доступа к этому приглашению",
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
                                            description = "Недостаточно прав для отклонения этого приглашения"
                                    )
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "Приглашение или участник не найдены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
            }
    )
    public void rejectInvitation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long invitationId
    ) {
        invitationService.rejectInvitation(invitationId, userDetails.getId());
    }
}
