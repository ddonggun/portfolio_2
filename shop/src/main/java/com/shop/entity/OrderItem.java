package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class OrderItem extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 하나의 상품은 여러 주문상품으로 들어갈수있으므로, 주문상품 기준 다대일 단방향 매핑 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 한번의 주문에 여러 상품을 주문할수 있으므로, 주문상품 엔티티와 주문 엔티티를 다대일 단방향 매핑을 먼저 설정

    private int orderPrice; //주문가격

    private int count; //수량

    //private LocalDateTime regTime; // BaseEntity 상속으로 해결

    //private LocalDateTime upDateTime; // BaseEntity 상속으로 해결

    //주문할 상품과 주문 수량을 통해 OrderItem 객체를 만드는 메소드 작성
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);//주문할 상품과 주문 수량을 세팅
        orderItem.setCount(count);//주문할 상품과 주문 수량을 세팅
        orderItem.setOrderPrice(item.getPrice());//현재시간 기준으로 상품 가격을 주문가격으로 세팅. 상품 가격은 시간에 따라 달라질 수 있고 쿠폰이나 할인을
        //적용하는 케이스도 있지만 여기서는 고려하지 않음
        item.removeStock(count);//주문 수량만큼 상품의 재고 수량을 감소시킴
        return orderItem;
    }
    public int getTotalPrice(){
        return orderPrice*count;//주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산하는 메소드
    }

    public void cancel(){//주문 취소 시 주문 수량만큼 상품
        this.getItem().addStock(count);
    }

}