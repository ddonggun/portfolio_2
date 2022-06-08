package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  //JPA의 Auditing 기능을 활성화. Auditing은 엔티티가 저장/수정될 때
//자동으로 등록일/수정일/등록자/수정자를 입력해줌.
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() { //등록자와 수정자를 처리해주는 AuditorAware을 빈으로 등록
        return new AuditorAwareImpl();
    }

}