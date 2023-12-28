package JWTpractice.JWTpracticespring.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignInDto {
    private String username;
    private String password;
}
