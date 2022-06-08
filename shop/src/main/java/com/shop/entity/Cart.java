package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart{
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) //@OneToOne 어노테이션으로 회원 엔티티와 일대일 매핑
    @JoinColumn(name="member_id") //@JoinColumn 어노테이션으로 매핑할 외래키를 지정
    //name 속성에는 매핑할 외래키의 이름을 설정
    //name을 명시하지 않으면 JPA가 알아서 ID를 찾지만 컬럼명이 원하는대로 생성되지 않을 수 있어 직접 지정
    private Member member;

    //회원 1명당 1개의 장바구니를 갖으므로 처음 장바구니에 상품을 담을때는
    //해당 회원의 장바구니를 생성해줘야함
    //Cart 클래스에 회원 엔티티를 파라미터로 받아서 장바구니 엔티티를 생성하는 로직 추가
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}