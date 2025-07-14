package doc.com.flexvibeproject.user.profile.dto;

import doc.com.flexvibeproject.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
