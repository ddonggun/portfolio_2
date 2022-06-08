package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import com.shop.service.CartService;
import com.shop.service.MemberService;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


//주문 관련 요청들을 처리하기 위한 클래스. 상품 주문에서 웹페이지의 새로고침 없이 서버에 주문을 요청하기 위해 비동기 방식 사용
@Controller
@RequiredArgsConstructor
@Transactional //private final ~~ 2개 이상 사용하기 위해 선언 (?)
public class OrderController{

    private final OrderService orderService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final CartService cartService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto
            , BindingResult bindingResult, Principal principal){
        //스프링에서 비동기 처리를 할때 @RequestBody와 @ResponseBody 어노테이션을 사용
        //@RequestBody: HTTP 요청의 본문 body의 담긴 내용을 자바 객체로 전달 / @ResponseBody:자바 객체를 HTTP요청의 body로 전달

        if(bindingResult.hasErrors()){
            //주문 정보를 받는 orderDto객체에 데이터 바인딩 시 에러가 있는지 검사
            //데이터 바인딩 : 화면에 보이는 데이터와 브라우저 메모리에 있는 데이터를 일치시키는 기법
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);//에러 정보를 ResponseEntity 객체에 담아 변환
        }

        String email = principal.getName();//현재 로그인 유저의 정보를 얻기 위해서 @Controller어노테이션이 선언된 클래스에서
        //메소드 인자로 principal 객체를 넘겨 줄 경우, 해당 객체에 직접 접근이 가능. principal객체에서 현재 로그인한 회원의 이메일 정보 조회
        Long orderId;
        try {
            orderId = orderService.order(orderDto, email);//화면으로부터 넘어오는 주문 정보화 회원의 이메일 정보를 이용하여 주문 로직 호출
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);//결과값으로 생성된 주문 번호와 요청이 성공했다는 Http응답상태 코드 반환
    }

    //구매이력을 조회할 수 있도록 OrderController클래스에 지금까지 구현한 로직을 호출하는 메소드 생성
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        //한번에 가지고 올 주문의 개수는 4개
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);
        //현재 로그인한 회원은 이메일 페이징 객체를 파라미터로 전달하여 화면에 전달한 주문 목록 데이터를 리턴값으로 받음. principal.getName() 값은 현재 로그인한 이메일주소.

        System.out.println("**********************************");
        System.out.println(principal.getName());
        System.out.println("**********************************");

        model.addAttribute("orders", ordersHistDtoList); //orderHist.html 의 "orders" 에 orderHistDtoList 객체를 매핑한다
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    @GetMapping(value = "/memberInfo")
    public String memberList(@PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        List<Member> members = memberService.findMembers();

        System.out.println("***********************");
        System.out.println(members);
        System.out.println("***********************");
        model.addAttribute("members", members);

        return "member/memberInfo";
    }

    //주문번호(orderId) 를 받아서, 주문 취소 로직을 호출하는 메소드 생성. 상품을 장바구니에 담았을때처럼 비동기 요청을 받아서 처리
    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){

        if(!orderService.validateOrder(orderId, principal.getName())){
            //자바스크립트에서 취소할 주문 번호는 조작이 가능하므로, 다른사람의 주문을 취소하지 못하도록 주문 취소 권한을 검사
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId); //주문 취소 로직을 호출
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}