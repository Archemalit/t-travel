package ru.tbank.itis.tripbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.itis.tripbackend.model.Notification;
import ru.tbank.itis.tripbackend.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserIdOrderByIdDesc(Long userId);
    long countByUserIdAndIsReadFalse(Long userId);
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}