package ru.tbank.itis.tripbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.request.UserUpdateProfileRequest;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.dto.response.UserProfileResponse;
import ru.tbank.itis.tripbackend.dto.response.ValidationErrorResponse;
import ru.tbank.itis.tripbackend.security.details.UserDetailsImpl;
import ru.tbank.itis.tripbackend.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "API для просмотра и обновления профиля пользователя")
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Получение данных профиля текущего пользователя",
            description = "Возвращает информацию о профиле авторизованного пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Профиль успешно загружен",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Пользователь не авторизован",
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
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public UserProfileResponse getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserProfile(userDetails.getId());
    }

    @PatchMapping("/edit")
    @Operation(
            summary = "Обновление профиля пользователя",
            description = "Позволяет пользователю обновить свои данные: имя, фамилия, номер телефона",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Профиль успешно обновлён",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации данных запроса",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Пользователь не авторизован",
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
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public UserProfileResponse updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserUpdateProfileRequest request) {
        return userService.updateProfile(userDetails.getId(), request);
    }
}
