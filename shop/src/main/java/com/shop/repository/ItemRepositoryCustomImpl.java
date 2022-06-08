package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;


//ItemRepositoryCustom 인터페이스 구현하기 , Impl을 붙여야 정상적으로 작동
//BooleanExpression이라는 where절에서 사용할수있는 값을 지원. BooleanExpression을 반환하는 메서드를 만들고 다른 쿼리 생성시 사용

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom { //1.

    private JPAQueryFactory queryFactory; //2.동적으로 쿼리를 생성하기 위해 JPAQueryFactory클래스 사용

    public ItemRepositoryCustomImpl(EntityManager em) { //3.JPAQueryFactory의 생성자로 EntityManager 객체를 넣어줌
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        //4. 상품 판매 상태 조건이 전체(NULL) 일때는 NULL 리턴. 결과값이 NULL이면 where 절에서 해당 조건이 무시됨
        //상품 판매 상태 조건이 NULL이 아니라 판매중/품절 상태라면 해당 조건의 상품만 조회
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        //5. searchDateType의 값에 따라서 dateTime의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회
        // 예를 들어 searchDateType 값이 1m 인 경우 dateTime의 시간을 한달 전으로 세팅 후 최근 한달 동안 등록된 상품만 조회하도록 조건값을 변경

        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        //6.searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에 검색어를 포함하고 있는 상품을 조회하도록 조건값을 반환

        if (StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
}

        return null;
                }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QueryResults<Item> results = queryFactory //7.쿼리 생성 
                .selectFrom(QItem.item)//상품데이터 조회를 위해 QItem의 item을 지정
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),//where 조건절: BooleanExpression 반환하는 조건문들을 넣음. , 단위는 and조건으로 인식
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())//데이터를 가지고 올 인덱스를 지정
                .limit(pageable.getPageSize())//한번에 가지고 올 최대 개수를 지정
                .fetchResults();//조회한 리스트 및 전체 개수를 포함하는 QueryResults를 반환, 상품 데이터 리스트 조회 및 상품 데이터 전체 개수를 조회하는
        // 2번의 쿼리문이 실행됨

        List<Item> content = results.getResults();
        long total = results.getTotal();
        
        return new PageImpl<>(content, pageable, total); //8.조회한 데이터를 Page클래스의 구현체인 PageImpl로 반환
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
        //검색어가 null이 아니면, 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto(
                                //QMainItemDto의 생성자에 반환할 값들을 넣어줌 @QueryProjection을 사용하면 DTO로 바로 조회 가능
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)//itemImg와 item을 내부 조인
                .where(itemImg.repimgYn.eq("Y"))//상품 이미지의 경우 대표 이미지만 불러옴
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}