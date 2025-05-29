package ru.tbank.itis.tripbackend.mapper;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.ExpenseDto;
import ru.tbank.itis.tripbackend.dto.ExpenseParticipantDto;
import ru.tbank.itis.tripbackend.model.Expense;
import ru.tbank.itis.tripbackend.model.ExpenseParticipant;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-29T16:16:50+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ExpenseMapperImpl implements ExpenseMapper {

    @Override
    public ExpenseDto toDto(Expense expense) {
        if ( expense == null ) {
            return null;
        }

        ExpenseDto.ExpenseDtoBuilder expenseDto = ExpenseDto.builder();

        expenseDto.id( expense.getId() );
        expenseDto.description( expense.getDescription() );
        expenseDto.participants( expenseParticipantSetToExpenseParticipantDtoSet( expense.getParticipants() ) );

        return expenseDto.build();
    }

    @Override
    public Expense toEntity(ExpenseDto expenseDto) {
        if ( expenseDto == null ) {
            return null;
        }

        Expense.ExpenseBuilder expense = Expense.builder();

        expense.description( expenseDto.getDescription() );

        return expense.build();
    }

    protected ExpenseParticipantDto expenseParticipantToExpenseParticipantDto(ExpenseParticipant expenseParticipant) {
        if ( expenseParticipant == null ) {
            return null;
        }

        ExpenseParticipantDto.ExpenseParticipantDtoBuilder expenseParticipantDto = ExpenseParticipantDto.builder();

        expenseParticipantDto.id( expenseParticipant.getId() );
        expenseParticipantDto.amount( expenseParticipant.getAmount() );

        return expenseParticipantDto.build();
    }

    protected Set<ExpenseParticipantDto> expenseParticipantSetToExpenseParticipantDtoSet(Set<ExpenseParticipant> set) {
        if ( set == null ) {
            return null;
        }

        Set<ExpenseParticipantDto> set1 = new LinkedHashSet<ExpenseParticipantDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( ExpenseParticipant expenseParticipant : set ) {
            set1.add( expenseParticipantToExpenseParticipantDto( expenseParticipant ) );
        }

        return set1;
    }
}
