package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

import com.shop.exception.OutOfStockException;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
//item 클래스가 가지고 있어야 할 멤버 변수들

    @Id //기본키
    @Column(name = "item_id") // item_id 에 매핑
    @GeneratedValue(strategy = GenerationType.AUTO) //기본키 자동 생성
    private Long id; //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name="price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태
    private LocalDateTime regTime; //등록 시간
    private LocalDateTime updateTime; //수정 시간

    //상품 데이터를 업데이트하는 로직. 엔티티클래스에 비즈니스 로직을 추가하면 더욱 객체지향이 가능. 코드 재활용성 증가
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    //상품을 주문 할 경우 상품의 재고를 감소시키는 로직
    public void removeStock(int stockNumber){
        int resetStock = this.stockNumber - stockNumber;
        if(resetStock<0){
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량:"+this.stockNumber+")");
        }
        this.stockNumber = resetStock;
    }

    //상품의 재고를 더해주기 위해서 Item클래스에 addStock 메소드 생성

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }


}
