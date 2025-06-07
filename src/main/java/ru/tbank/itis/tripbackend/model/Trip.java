package ru.tbank.itis.tripbackend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Accessors(chain = true)
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, name = "total_budget")
    private Double totalBudget;

    @ManyToOne()
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForTripAndInvitationStatus status;

    @OneToMany(mappedBy = "trip")
    private List<Expense> expenses;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private Set<TripParticipant> participants = new HashSet<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private Set<Debt> debts = new HashSet<>();

//    @OneToMany(mappedBy = "trip")
//    private Set<Notification> notifications = new HashSet<>();

}