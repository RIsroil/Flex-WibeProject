package doc.com.flexvibeproject.config;

import doc.com.flexvibeproject.user.Role;
import doc.com.flexvibeproject.user.UserEntity;
import doc.com.flexvibeproject.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "aaa";
        String adminPassword = "aaa";
        String username = "aaa";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setEmail(adminEmail);
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✔ Admin user created: " + adminEmail);
        }
    }
}
