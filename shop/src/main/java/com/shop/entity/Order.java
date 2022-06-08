package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //정렬할때 사용하는 order 키워드가 잇기때문에 Order엔티티에 매핑되는 테이블로 "orders" 지정
@Getter @Setter
public class Order{


    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //한명의 회원은 여러번 주문 가능. 주문 엔티티 기준 다대일 단방향 매핑 적용

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // orphanRemoval = true : 고아 객체 제거 테스트
    // cascadeType.ALL : 영속성 상태변화를 자식 엔티티에게 모두 전이하는 옵션
    /* 주문 상품 엔티티와 일대다 매핑.왜래키(order_id)가 order_item 테이블에 있으므로, 연관 관계의 주인은 OrderItem 엔티티.
    mappedBy 속성으로 연관 관계의 주인을 설정함. 속성값으로 order 를 적어준 이유는 OrderItem 의 Order 에 의해 관리된다는 의미.
    연관 관계의 주인의 필드인 order 를 mappedBy 의 값으로 세팅하면 됨 */
    private List<OrderItem> orderItems = new ArrayList<>();
    //하나의 주문이 여러 주문상품을 갖으므로 List 자료형을 사용해서 매핑
    
    //private LocalDateTime regTime;

    //private LocalDateTime upDateTime;

    //생성한 주문 상품 객체를 이용하여 주문 객체를 만드는 메소드 작성↓
    public void addOrderItem(OrderItem orderItem) {
        //1. orderItem에는 주문 상품 정보들을 담음. orderItem객체를 order객체의 orderItems에 추가
        orderItems.add(orderItem);
        orderItem.setOrder(this);//Order엔티티와 OrderItem엔티티가 양방향 참조관계이므로, orderItem객체에도 order객체를 세팅
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);//상품을 주문한 회원의 정보를 세팅

        for(OrderItem orderItem : orderItemList) {//상품 페이지에서는 1개의 상품을 주문하지만, 장바구니 페이지에서는 한번에 여러 상품 주문 가능
            //여러개의 주문 상품을 담을 수 있도록, 리스트형태로 파라미터 값을 받으며 주문 객체에 orderItem객체를 추가
            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);//주문 상태를 ORDER로 세팅
        order.setOrderDate(LocalDateTime.now());//주문 시간을 현재시간으로 세팅
        return order;
    }

    public int getTotalPrice() {//총 주문 금액을 구하는 메소드
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }


    //Item클래스에 주문 취소 시 주문 수량을 상품 재고에 더해주는 로직 및 주문상태를 취소 상태로 바꿔주는 메소드를 구현

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }


}