package JWTpractice.JWTpracticespring;

import JWTpractice.JWTpracticespring.controller.MemberController;
import JWTpractice.JWTpracticespring.repository.MemberRepository;
import JWTpractice.JWTpracticespring.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwTpracticeSpringApplicationTests {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberController memberController;
    @Autowired
    private MockMvc mockMvc;
    @Test
    void 회원가입_및_로그인_및_접근_연계테스트() throws Exception {
        memberService.join("sungkyu", "1234", "성규");
        String accessToken = memberService.signIn("sungkyu", "1234").getAccessToken();

        mockMvc.perform(get("/members/test").header("Authorization", "Bearer " + accessToken))
                .andExpect(content().string("success"));
    }


}