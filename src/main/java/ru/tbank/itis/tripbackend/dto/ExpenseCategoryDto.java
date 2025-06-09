package ru.tbank.itis.tripbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryDto {

    private Long id;

    @NotBlank(message = "Название категории обязательно")
    @Size(max = 100, message = "Название категории должно быть менее 100 символов")
    private String title;

}
