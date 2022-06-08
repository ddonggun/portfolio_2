package com.shop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

//Audit : 감시하다. 엔티티의 생성과 수정을 감시. 
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";
        if(authentication != null){
            userId = authentication.getName(); //현재 로그인한 사용자의 정보를 조회. 사용자의 이름을 등록자와 수정자로 지정
        }
        return Optional.of(userId);
    }

}