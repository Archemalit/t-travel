package ru.tbank.itis.tripbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.itis.tripbackend.dictonary.ExpenseCategory;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "actual_expenses")
public class ActualExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "trip_id")
    private Long tripId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, name = "cheque_image")
    private String chequeImage;

    @Column(nullable = false, name = "paid_by_user_id")
    private Long paidByUserId;
}
