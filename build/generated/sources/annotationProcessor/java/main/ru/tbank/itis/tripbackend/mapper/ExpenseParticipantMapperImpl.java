package ru.tbank.itis.tripbackend.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.ExpenseParticipantDto;
import ru.tbank.itis.tripbackend.model.ExpenseParticipant;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-29T16:16:50+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class ExpenseParticipantMapperImpl implements ExpenseParticipantMapper {

    @Override
    public ExpenseParticipantDto toDto(ExpenseParticipant expenseParticipant) {
        if ( expenseParticipant == null ) {
            return null;
        }

        ExpenseParticipantDto.ExpenseParticipantDtoBuilder expenseParticipantDto = ExpenseParticipantDto.builder();

        expenseParticipantDto.id( expenseParticipant.getId() );
        expenseParticipantDto.amount( expenseParticipant.getAmount() );

        return expenseParticipantDto.build();
    }

    @Override
    public ExpenseParticipant toEntity(ExpenseParticipantDto expenseParticipantDto) {
        if ( expenseParticipantDto == null ) {
            return null;
        }

        ExpenseParticipant.ExpenseParticipantBuilder expenseParticipant = ExpenseParticipant.builder();

        expenseParticipant.amount( expenseParticipantDto.getAmount() );

        return expenseParticipant.build();
    }
}
