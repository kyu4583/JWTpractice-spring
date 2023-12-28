package JWTpractice.JWTpracticespring.repository;

import JWTpractice.JWTpracticespring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //JPA 리포지토리에서 기본적으로 구현되는 다양한 메서드들이 뒤로 존재함.
    Optional<Member> findByUsername(String username); // 얘는 직접 선언까진 해줘야함. 필드 이름까진 미리 모르니까. 기능은 유추해서 짜줌.

    List<Member> findByNickname(String nickname);
}
