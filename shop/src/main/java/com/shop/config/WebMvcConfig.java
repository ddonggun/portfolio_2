package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//addResourceHandlers 메소드를 통해 자신의 로컬 컴퓨터에 업로드한 파일을 찾을 위치를 설정
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")// application.properties에 설정된 uploadPath 프로퍼티값 읽기
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // url이 이렇게 시작하는경우 위에서 설정한 폴더 기준으로 파일 읽기
                .addResourceLocations(uploadPath); //로컬 컴퓨터에 저장된 파일을 읽어올 root 경로설정
    }

}