package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity{ //장바구니를 위해 BaseEntity 상속
    
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;  //1 장바구니 n 상품 이므로, @ManyToOne어노테이션을 이용하여 다대일 관계로 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 장바구니에 담을 상품의 정보를 알아야하므로 상품 엔티티를 매핑,
    //한 상품은 여러장바구니의 장바구니 상품으로 담길 수 있으므로, 마찬가지로 @ManyToOne 로 매핑.

    private int count; // 같은 상품을 장바구니에 총 몇개 담았는지?
    
    //장바구니에 담을 상품 엔티티를 생성하는 메소드와 장바구니에 담을 수량을 증가시켜주는 메소드를 CartItem 클래스에 추가
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }
    public void addCount(int count){ //장바구니에 기존에 담겨 있는 상품인데, 해당 상품을 추가로 장바구니에 담을 때
        //기존 수량에 현재 담을 수량을 더해줄 때 사용할 메소드
        this.count += count;
    }

    //장바구니에서 상품의 수량을 변경할 경우 실시간으로 해당 회원의 자압구니 상품의 수량도 변경하도록 로직에 추가
    //현재 장바구니에 담겨있는 수량을 변경하는 메소드 입니다.
    public void updateCount(int count){
        this.count = count;
    }
    
}