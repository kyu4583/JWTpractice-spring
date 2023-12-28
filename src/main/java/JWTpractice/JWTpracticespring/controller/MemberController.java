package JWTpractice.JWTpracticespring.controller;

import JWTpractice.JWTpracticespring.dto.JoinDto;
import JWTpractice.JWTpracticespring.dto.JwtToken;
import JWTpractice.JWTpracticespring.dto.SignInDto;
import JWTpractice.JWTpracticespring.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public void join(@RequestBody JoinDto joinDto) {
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        String nickname = joinDto.getNickname();
        log.info("request username = {}, password = {}, nickname = {}", username, password, nickname);
        memberService.join(username, password, nickname);
    }

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(username, password);
        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @GetMapping("/test")
    public String test() {
        return "success";
    }

}