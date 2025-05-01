package ru.tbank.itis.tripbackend.event;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tbank.itis.tripbackend.dictonary.UserRole;
import ru.tbank.itis.tripbackend.model.User;
import ru.tbank.itis.tripbackend.repository.UserRepository;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class ApplicationReadyEventListener {
    private static final String ADMIN_PHONE_NUMBER = "7999999999";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (userRepository.findUserByPhoneNumber(ADMIN_PHONE_NUMBER).isEmpty()) {
            User user = new User()
                    .setFirstName("Name")
                    .setLastName("Surname")
                    .setPhoneNumber(ADMIN_PHONE_NUMBER)
                    .setPassword(passwordEncoder.encode("123"))
                    .setRole(UserRole.ADMIN);

            userRepository.save(user);
        }
    }

}