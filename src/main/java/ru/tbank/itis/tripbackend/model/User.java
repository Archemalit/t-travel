package ru.tbank.itis.tripbackend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.tbank.itis.tripbackend.dictionary.UserRole;

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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private List<Trip> createdTrips;

    @OneToMany(mappedBy = "paidBy", fetch = FetchType.LAZY)
    private List<Expense> paidExpenses;

//    @OneToMany(mappedBy = "user")
//    private Set<Notification> notifications = new HashSet<>();
}
