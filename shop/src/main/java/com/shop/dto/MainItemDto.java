package com.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;


/*
@QueryProjection을 이용하면 Item객체로 값을 받은 후 Item->Dto 클래스 변환 없이 바로 DTO객체를 뽑아낼 수 있습니다.
아래는 메인 페이지에서 상품을 보여줄 때 사용할 MainItemDto 클래스 입니다.
 */
@Getter @Setter
public class MainItemDto {

    private Long id;

    private String itemNm;

    private String itemDetail;

    private String imgUrl;

    private Integer price;

    @QueryProjection //생성자에 @QueryProjection 어노테이션을 선언하며 Querydsl로 결과 조회 시 MainItemDto객체로 바로 받아올 수 있음
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl,Integer price){
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }

}