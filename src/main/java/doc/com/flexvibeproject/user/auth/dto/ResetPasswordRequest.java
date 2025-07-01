package doc.com.flexvibeproject.user.auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
    private String confirmPassword;

}
