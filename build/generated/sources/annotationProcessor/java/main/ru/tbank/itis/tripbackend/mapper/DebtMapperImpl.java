package ru.tbank.itis.tripbackend.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dto.DebtDto;
import ru.tbank.itis.tripbackend.model.Debt;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-23T23:40:17+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class DebtMapperImpl implements DebtMapper {

    @Override
    public DebtDto toDto(Debt debt) {
        if ( debt == null ) {
            return null;
        }

        DebtDto.DebtDtoBuilder debtDto = DebtDto.builder();

        debtDto.id( debt.getId() );
        debtDto.amount( debt.getAmount() );

        return debtDto.build();
    }

    @Override
    public Debt toEntity(DebtDto debtDto) {
        if ( debtDto == null ) {
            return null;
        }

        Debt.DebtBuilder debt = Debt.builder();

        debt.amount( debtDto.getAmount() );

        return debt.build();
    }
}
