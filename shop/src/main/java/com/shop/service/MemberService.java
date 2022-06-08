package com.shop.service;

import com.shop.constant.Role;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@Service
@Transactional //비즈니스 로직 담당 서비스계층클래스에 @Transactional 어노테이션 선언 , 로직 처리중 에러 발생 시 로직 수행 이전으로 롤백
@RequiredArgsConstructor
//@RequiredArgsConstructor -> final이나 NonNull이 붙은 필드에 생성자 생성.
public class MemberService implements UserDetailsService{//MemberService는 UserDetailsService를 구현합니다

    private final MemberRepository memberRepository;
    //빈에 생성자가 1개이고 생성자의 파라미터 타입이 빈으로 등록 가능하면 @Autowired어노테이션은 생략하고 의존성 주입 가능

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//UserDetailsService 인터페이스의 loadUserByUsername()메소드를 오버라이딩 합니다.
//로그인할 유저의 email을 파라미터로 전달받습니다
        Member member = memberRepository.findByEmail(email);

        if(member == null){
        throw new UsernameNotFoundException(email);
    }

    //UserDetail을 구현하고 있는 User객체를 반환
    //User객체를 생성하기 위해 생성자로 회원의 이메일, 비밀번호, role을 파라미터로 넘겨줌
        return User.builder() //아래 원하는 객체만 골라서 반환함
                .username(member.getEmail())
            .password(member.getPassword())
            .roles(member.getRole().toString())
            .build();
}

    public List<Member> findMembers() {
            return memberRepository.findAll();
    }

}