package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

//상품 데이터 정보를 전달하는 DTO
@Getter @Setter
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "상품 상세는 필수 입력 값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();
    //상품 저장 후 수정할때, 상품의 이미지정보를 저장하는 리스트

    private List<Long> itemImgIds = new ArrayList<>();
    //상품의 이미지 아이디를 저장하는 리스트. 상품등록시에는 아직 상품의 이미지를 등록하지 않았기 때문에, 값이 없고
    //수정 시에 이미지 아이디를 담아둘 용도로 사용
    private static ModelMapper modelMapper = new ModelMapper();

    
    public Item createItem(){
        return modelMapper.map(this, Item.class);
        //modelMapper를 이용하여 엔티티 객체와 DTO 객체간의 데이터를 복사 후, 복사한 객체를 반환해주는 메소드
    }

    public static ItemFormDto of(Item item){
        return modelMapper.map(item,ItemFormDto.class);
        //modelMapper를 이용하여 엔티티 객체와 DTO 객체간의 데이터를 복사 후, 복사한 객체를 반환해주는 메소드
    }

}