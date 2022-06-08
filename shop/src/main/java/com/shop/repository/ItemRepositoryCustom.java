package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    //1. 상품 조회 조건을 담고있는 itemSearchDto 객체와 페이징정보를 담고있는 pageable 객체를 파라미터로 받는
    //getAdminItemPage 메서드를 정의. 반환 데이터로 Page<Item> 객체를 반환


    //메인페이지에 보여줄 상품 리스트를 가져오는 메서드
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}