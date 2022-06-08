package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

//ItemRepositoryCustom 상속시, Querydsl로 구현한 상품 관리 페이지 목록을 불러오는 getAdminItemPage() 메서드를 사용 가능
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    List<Item> findByItemNm(String itemNm);
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    //LessThan 조건 처리하기
    List<Item> findByPriceLessThan(Integer price);

    //내림차순(높은 가격 순)으로 조회하기
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    //JPQL로 작성한 쿼리문
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    //통계용 쿼리처럼 복잡한 쿼리의 경우 기존쿼리대로 처리한다 -> nativeQuery = true
    @Query(value="select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}
