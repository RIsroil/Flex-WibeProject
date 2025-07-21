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
        if (userRepository.findByEmail("aaa").isEmpty()) {
            UserEntity admin1 = new UserEntity();
            admin1.setEmail("aaa");
            admin1.setUsername("aaa");
            admin1.setPassword(passwordEncoder.encode("aaa"));
            admin1.setRole(Role.ADMIN);
            userRepository.save(admin1);

            UserEntity admin2 = new UserEntity();
            admin2.setEmail("creed@gmail.com");
            admin2.setUsername("creed");
            admin2.setPassword(passwordEncoder.encode("creeddev"));
            admin2.setRole(Role.ADMIN);
            userRepository.save(admin2);

            System.out.println("✔ Admin user created: " + "aaa");
            System.out.println("✔ Admin user created: " + "creed@gmail.com");
        }
    }
}
