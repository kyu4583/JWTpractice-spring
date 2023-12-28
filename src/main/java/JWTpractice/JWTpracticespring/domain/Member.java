package JWTpractice.JWTpracticespring.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity //(Lombok)JPA 엔티티임을 나타냄
@Getter //(Lombok)필드의 getter 메서드를 자동으로 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //(Lombok)기본 생성자(파라미터 안받는)를 자동으로 추가. AccessLevel.PROTECTED로 했으니 외부에서는 생성자를 사용할 수 없음.
@AllArgsConstructor //(Lombok)모든 필드 값을 파라미터로 받는 생성자를 자동으로 생성
@Builder    //(Lombok)빌더 패턴 클래스를 자동으로 생성(선택적 매개변수를 가진 생성자를 만들어줌 등)
@EqualsAndHashCode(of = "id")   //equals 메서드와 hashCode 메서드를 자동으로 생성. of = "id"로 지정해주면 id 값만 같으면 같은 객체로 판단하도록 함.
public class Member implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "member_id", unique = true, nullable = false)
    private Long id;    //PK, DB 식별용

    @Column(nullable = false)
    private String password;
    private String username;    //ID, 로그인 식별용
    private String nickname;    //닉네임, 후보키가 아님, 중복 가능

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.username = name;
    }

    @ElementCollection(fetch = FetchType.EAGER) //얘가 있어서 List<String>을 필드로 가질 수 있음(JPA).
    @Builder.Default    //@Builder를 사용할 때(Lombok의 빌더를 쓸 때) 기본값을 설정해줌.
    private List<String> roles = new ArrayList<>();
    /*
    이 클래스 내에서 rolse를 채우는 코드는 없지만, 외부에서 Lombok 빌더를 사용할 때 채워진다고 가정함.
    예) Member member = Member.builder()
                      .username("user")
                      .password("password")
                      .roles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"))
                      .build();
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new) /*스트림의 각 요소(권한을 나타내는 문자열)를 SimpleGrantedAuthority 객체로 변환
                                                    SimpleGrantedAuthority가 문자열을 통해 생성되는 생성자를 가졌기에 가능한 코드*/
                .collect(Collectors.toList()); //변환된 객체들을 리스트로 변환
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}