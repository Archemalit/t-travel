package ru.tbank.itis.tripbackend.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.ExpenseCategoryDto;
import ru.tbank.itis.tripbackend.model.ExpenseCategory;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T00:12:30+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ExpenseCategoryMapperImpl implements ExpenseCategoryMapper {

    @Override
    public ExpenseCategoryDto toDto(ExpenseCategory expenseCategory) {
        if ( expenseCategory == null ) {
            return null;
        }

        ExpenseCategoryDto.ExpenseCategoryDtoBuilder expenseCategoryDto = ExpenseCategoryDto.builder();

        expenseCategoryDto.id( expenseCategory.getId() );
        expenseCategoryDto.title( expenseCategory.getTitle() );

        return expenseCategoryDto.build();
    }

    @Override
    public ExpenseCategory toEntity(ExpenseCategoryDto expenseCategoryDto) {
        if ( expenseCategoryDto == null ) {
            return null;
        }

        ExpenseCategory.ExpenseCategoryBuilder expenseCategory = ExpenseCategory.builder();

        expenseCategory.title( expenseCategoryDto.getTitle() );

        return expenseCategory.build();
    }
}
