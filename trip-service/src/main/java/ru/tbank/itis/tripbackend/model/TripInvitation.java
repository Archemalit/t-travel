package ru.tbank.itis.tripbackend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.tbank.itis.tripbackend.dictionary.ForTripAndInvitationStatus;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Accessors(chain = true)
@Table(name = "trip_invitations")
public class TripInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "invited_user_id", nullable = false)
    private User invitedUser;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForTripAndInvitationStatus status;

}