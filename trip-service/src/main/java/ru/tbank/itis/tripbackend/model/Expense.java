package ru.tbank.itis.tripbackend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.tbank.itis.tripbackend.dictionary.ExpenseCategory;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Accessors(chain = true)
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "paid_by_user_id", nullable = false)
    private User paidBy;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ExpenseParticipant> participants = new HashSet<>();

}
