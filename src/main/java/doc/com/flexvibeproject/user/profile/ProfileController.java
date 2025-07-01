package doc.com.flexvibeproject.user.profile;

import doc.com.flexvibeproject.user.profile.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(profileService.changePassword(principal, request));
    }
}
