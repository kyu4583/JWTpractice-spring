package JWTpractice.JWTpracticespring.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {
    private String username;
    private String password;
    private String nickname;

    public String getUsername() {
        return username;
    }
}
