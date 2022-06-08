package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


//장바구니와 관련된 요청들을 처리하기 위한 클래스
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal){

        //1. 장바구니에 담을 상품정보를 받는 cartItemDto 객체에 데이터 바인딩 시 에러가 있는지 검사

        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }


        String email = principal.getName(); //2. 현재 로그인한 회원의 이메일 정보를 변수에 저장
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);
            //3. 화면으로부터 넘어온 장바구니에 담을 상품 정보와 현재 로그인한 회원의 이메일 정보를 이용해 장바구니에 상품을 담는 로직 
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK); //4. 
        //결과값으로 생성된 장바구니 상품 아이디와 요청이 성공하였다는 HTTP 응답 상태 코드 반환
    }

    //장바구니 페이지로 이동할 수 있도록 메소드 추가
    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
        //1.현재 로그인한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품 정보 조회  
        model.addAttribute("cartItems", cartDetailList);
        //2.조회한 장바구니 상품 정보를 뷰 로 전달
        return "cart/cartList";
    }

    //장바구니 상품의 수량을 업데이트하는 요청을 처리할 수 있도록 로직을 추가
    @PatchMapping(value = "/cartItem/{cartItemId}")
    //HTTP 메소드에서 PATCH는 요청된 자원의 일부를 업데이트 할 때 PATCH를 사용.
    //장바구니 상품의 수량만 업데이트하기 때문에 @PatchMapping을 사용
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){

        if(count <= 0){ //장바구니에 담겨있는 상품의 개수를 0개 이하로 요청했을때 에러 메세지를 담아서 반환
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())){//수정 권한 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count); //장바구니 상품 개수 업데이트
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    //상품을 삭제하는 요청을 처리할 수 있는 로직
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    //HTTP메소드에서 DELETE의 경우 요청된 자원을 삭제할 때 사용됨. 장바구니 상품을 삭제하기 때문에 @DeleteMapping을 사용
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){

        if(!cartService.validateCartItem(cartItemId, principal.getName())){//수정 권한을 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId); //해당 장바구니 상품을 삭제

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    //장바구니 상품의 수량을 업데이트하는 요청을 처리할 수 있도록 로직 추가
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            //1.주문할 상품을 선택하지 않았는지 체크
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        for (CartOrderDto cartOrder : cartOrderDtoList) {
            //2. 주문 권한을 체크
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        //3. 주문 로직 호출 결과 생성된 주문 번호를 반환 
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
        //4. 생성된 주문 번호와 요청이 성공했다는 HTTP 응답 상태 코드를 반환
        
    }
    
}