package JWTpractice.JWTpracticespring.service;

import JWTpractice.JWTpracticespring.domain.Member;
import JWTpractice.JWTpracticespring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {   // username을 통해 해당하는 User의 데이터를 가져옴
        return memberRepository.findByUsername(username)    // Optional<Member> 타입으로 반환
                .map(this::createUserDetails)   // Optional<Member> 타입을 Optional<UserDetails> 타입으로 변환
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));   // Optional<Member> 타입이 비어있다면 예외 발생
    }

    // 해당하는 User의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(Member member) {
        return User.builder()
                .username(member.getUsername()) // username을 통해 해당하는 User의 데이터를 가져옴
                .password(member.getPassword()) // password를 통해 해당하는 User의 데이터를 가져옴
                .roles(member.getRoles().toArray(new String[0]))    // roles를 통해 해당하는 User의 데이터를 가져옴
                .build();   // UserDetails 객체 생성
    }

}