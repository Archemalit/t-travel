package ru.tbank.itis.tripbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tbank.itis.tripbackend.dto.JwtTokenPairDto;
import ru.tbank.itis.tripbackend.dto.request.UserLoginRequest;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;
import ru.tbank.itis.tripbackend.dto.response.SimpleErrorResponse;
import ru.tbank.itis.tripbackend.dto.response.UserExistsResponse;
import ru.tbank.itis.tripbackend.dto.response.ValidationErrorResponse;
import ru.tbank.itis.tripbackend.service.UserService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для регистрации, авторизации и обновления токена")
@Validated
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя и возвращает пару JWT-токенов (access и refresh)",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Пользователь успешно зарегистрирован",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtTokenPairDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации данных запроса",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    )
            }
    )
    public JwtTokenPairDto register(@RequestBody @Valid UserRegistrationRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Вход пользователя",
            description = "Аутентифицирует пользователя по номеру телефона и паролю, возвращает пару JWT-токенов",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный вход",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtTokenPairDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные для аутентификации",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public JwtTokenPairDto login(@RequestBody @Valid UserLoginRequest request) {
        return null;
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Обновление JWT-токенов",
            description = "Использует refresh-токен из заголовка для выдачи новой пары токенов",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Токены успешно обновлены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtTokenPairDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Refresh-токен недействителен или истёк",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class))
                    )
            }
    )
    public JwtTokenPairDto refresh() {
        return null;
    }

    @GetMapping("/check")
    @Operation(
            summary = "Проверка наличия пользователя по номеру телефона",
            description = "Возвращает true, если пользователь с указанным номером телефона существует",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Результат проверки",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserExistsResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный формат номера телефона или отсутствует обязательный параметр",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    )
            }
    )
    public UserExistsResponse doesUserExistByPhoneNumber(
            @RequestParam @Pattern(regexp = "^7\\d{10}$",
                    message = "Номер телефона должен быть в формате 7XXXXXXXXXX") String phone) {
        return userService.doesUserExistByPhoneNumber(phone);
    }
}