package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name="member")
@Getter @Setter
@ToString
public class Member extends BaseEntity{//BaseEntity 클래스를 상속받고 있다.
    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        //Member 엔티티를 생성하는 메소드. Member엔티티에 회원을 생성하는 메서드를 만들어 관리한다면 코드가 변경되어도 한군데만 수정하면 되는 이점
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        //스프링시큐리티 설정 클래스에 등록한 BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호를 암호화함
        member.setPassword(password);
        if(memberFormDto.getAdmincode().equals("1357")) {
            member.setRole(Role.ADMIN);//ADMIN , USER 선택 , 권한 설정
        }else{
            member.setRole(Role.USER);//ADMIN , USER 선택 , 권한 설정
        }
        return member;
    }

}
