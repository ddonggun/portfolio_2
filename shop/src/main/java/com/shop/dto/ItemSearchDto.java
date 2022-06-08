package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {

    private String searchDateType;
    //all - 상품 등록일 전체
    //1d - 최근 하루동안 등록된 상품
    //1w - 최근 일주일 동안 등록된 상품
    //1m - 최근 한달 동안 등록된 상품
    //6m - 최근 6개월동안 등록된 상품

    private ItemSellStatus searchSellStatus;
    //상품의 판매상태를 기준으로 상품 데이터를 조회
    
    private String searchBy;
    //상품을 조회할 때 어떤 유형으로 조회할지 선택
    //itemNm: 상품명 //createBy: 상품 등록자 아이디
    
    private String searchQuery = "";
    //조회할 검색어 저장할 변수입니다. searchBy가 itemNm일경우 상품명을 기준으로검색
    //createdBy일 경우 상품 등록자 아이디 기준으로 검색
    

    //-> Querydsl을 SpringData JPA와 함께 사용하기 위해서는 사용자 정의 리포지토리를 정의해야함
    //1. 사용자 정의 인터페이스 작성
    //2. 사용자 정의 인터페이스 구현
    //3. Spring Data Jpa 리포지토리에서 사용자 정의 인터페이스 상속
}