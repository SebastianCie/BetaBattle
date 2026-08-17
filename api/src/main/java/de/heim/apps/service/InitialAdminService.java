package de.heim.apps.service;

import de.heim.apps.entity.User;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@ApplicationScoped
public class InitialAdminService {

    @Inject
    PasswordService passwordService;

    @ConfigProperty(name = "beta-battle.initial-admin.email", defaultValue = "admin@betabattle.local")
    String initialAdminEmail;

    @ConfigProperty(name = "beta-battle.initial-admin.password")
    Optional<String> initialAdminPassword;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if (User.count() > 0) return;

        String password = initialAdminPassword.orElseGet(InitialAdminService::generatePassword);

        User admin = new User();
        admin.email = initialAdminEmail;
        admin.passwordHash = passwordService.hash(password);
        admin.displayName = "Admin";
        admin.role = "ADMIN";
        admin.emailVerified = true;
        admin.persist();

        if (initialAdminPassword.isPresent()) {
            Log.infof("Initial admin user created: %s", initialAdminEmail);
        } else {
            Log.warnf("Initial admin user created: %s / %s — this password is shown only once, save it now.",
                    initialAdminEmail, password);
        }
    }

    private static String generatePassword() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
