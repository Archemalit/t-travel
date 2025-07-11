//package ru.tbank.itis.notificationservice;
//
//import jakarta.persistence.*;
//import lombok.*;
//import ru.tbank.itis.tripservice.dictionary.NotificationType;
//
//
//
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "notifications")
//public class Notification {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String message;
//
//    @Column(nullable = false)
//    private boolean isRead;
//
//    @Column(nullable = false)
//    private NotificationType type;
//
//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "trip_id", nullable = false)
//    private Trip trip;
//}