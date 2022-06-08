package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//주문 정보를 담을 OrderHistDto클래스 생성
@Getter @Setter
public class OrderHistDto {

    public OrderHistDto(Order order){ //OrderHistDto클래스의 생성자로 order객체를 파라미터로 받아 멤버변수값을 세팅
        //주문 날짜의 경우 yyyy-MM-dd HH:mm 형태로 전달하기 위해 포맷을 수정
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }
    private Long orderId; //주문아이디
    private String orderDate; //주문날짜
    private OrderStatus orderStatus; //주문 상태
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    //주문 상품리스트
    public void addOrderItemDto(OrderItemDto orderItemDto){ //orderItemDto 객체를 주문 상품 리스트에 추가하는 메소드입니다.
        orderItemDtoList.add(orderItemDto);
    }

}