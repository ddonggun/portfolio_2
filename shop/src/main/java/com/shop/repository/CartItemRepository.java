package com.shop.repository;

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//장바구니에 들어갈 상품을 저장하거나 조회하기 위한 인터페이스
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
    //카트 아이디와 상품 아이디를 이용해 상품이 장바구니에 들어있는지 조회

    //장바구니 페이지에 전달할 CartDetailDto 리스트를 쿼리 하나로 조회하는 JPQL문
    //연관관계 매핑을 지연로딩으로 설정할 경우 엔티티에 매핑된 다른 엔티티를 조회할 때 추가적으로 쿼리문이 실행됨.
    //성능 최적화가 필요할 경우 아래 코드와 같이 DTO의 생성자를 이용하여 반환값으로 DTO 객체 생성 가능
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            //CartDetailDto의 생성자를 이용하여 Dto 를 반환할 때는 위와같이
            //new 키워드와 해당 Dto의 패키지, 클래스명을 적어줌. 또한 생성자의 파라미터 순서는 DTO클래스에 명시한 순으로 넣어야함
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " + //장바구니에 담겨있는 상품의 대표이미지만 가지고 오도록 조건문 작성
            "and im.repimgYn = 'Y' " + //장바구니에 담겨있는 상품의 대표이미지만 가지고 오도록 조건문 작성
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

}