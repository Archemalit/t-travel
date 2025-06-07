//package ru.tbank.itis.tripbackend.mapper;
//
//import org.mapstruct.Mapper;
//import ru.tbank.itis.tripbackend.dto.ActualExpenseDto;
//import ru.tbank.itis.tripbackend.mapper.impl.CustomActualExpenseMapper;
//import ru.tbank.itis.tripbackend.model.ActualExpense;
//import ru.tbank.itis.tripbackend.model.User;
//
//import java.util.Set;
//
//import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
//
//@Mapper(componentModel = SPRING, uses = {UserMapper.class, CustomActualExpenseMapper.class})
//public interface ActualExpenseMapper {
//    ActualExpenseDto toDto(ActualExpense expense);
//
//    ActualExpense toEntity(ActualExpenseDto expenseDto, Set<User> members);
//}
