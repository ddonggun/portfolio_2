package com.shop.repository;

import com.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


//@Query 어노테이션을 이용해 주문 이력을 조회하는 쿼리 작성
//@Query 안에 들어가는 문법은 JPQL
//조회 조건이 복잡하지 않으면 QueryDsl을 사용하지 않고 @Query 어노테이션을 이용해서 구현하는것도 괜찮음
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc"
    )
    List<Order> findOrders(@Param("email") String email, Pageable pageable);
    //현재 로그인한 사용자의 주문 데이터를 페이징 조건에 맞춰서 조회
    @Query("select count(o) from Order o " +
            "where o.member.email = :email"
    )
    Long countOrder(@Param("email") String email);
    //현재 로그인한 회원의 주문 개수가 몇개인지 조회
    @Query("select o from Order o " +
            "where o.member.id = :id " +
            "order by o.orderDate desc"
    )
    List<Order> findOrdersById(@Param("id") Long id, Pageable pageable);
    //id에 맞는 조회
    @Query("select count(o) from Order o " +
            "where o.member.id = :id"
    )
    Long countOrderById(@Param("id") Long id);
    //현재 로그인한 회원의 주문 개수가 몇개인지 조회
}