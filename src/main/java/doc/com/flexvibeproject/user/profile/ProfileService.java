package doc.com.flexvibeproject.user.profile;


import doc.com.flexvibeproject.user.jwt.JwtService;
import doc.com.flexvibeproject.user.UserEntity;
import doc.com.flexvibeproject.user.UserRepository;
import doc.com.flexvibeproject.user.auth.AuthHelperService;
import doc.com.flexvibeproject.user.profile.dto.ChangePasswordRequest;
import doc.com.flexvibeproject.user.profile.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthHelperService authHelperService;

    public ResponseEntity<?> changePassword( Principal principal, ChangePasswordRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Eski parol noto‘g‘ri");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Parol muvaffaqiyatli yangilandi");
    }

    public UserProfileResponse me(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
