package doc.com.flexvibeproject.user.profile.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}