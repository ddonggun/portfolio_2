package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.entity.Member;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;




//상품을 등록하는 클래스 입니다.
//ItemService 클래스에 상품 조회 조건과 페이지 정보를 파라미터로 받아서 상품 데이터를 조회하는 getAdminItemPAge() 메소드를 추가
//이는 데이터의 수정은 일어나지 않으므로 -> Transactional(readOnly=true) 어노테이션 설정으로 성능 향상
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품 등록
        Item item = itemFormDto.createItem(); //상품 등록 폼으로부터 입력받은 데이터를 이용하여 item 객체 생성
        itemRepository.save(item); //상품 데이터를 저장

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0)//첫번째 이미지일 경우 대표 상품 이미지 여부 값을 Y로 세팅 나머지 상품 이미지는 N으로 설정
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
            //상품의 이미지 정보를 저장합니다
        }

        return item.getId();
    }

    //등록된 상품을 불러오는 메소드를 ItemService에 추가하겠습니다
    @Transactional(readOnly = true) //상품 데이터를 읽어오는 트랜잭션을 읽기 전용으로 설정 - JPA가 더티체킹(변경감지) 를 수행하지 않아 성능이 향상됨
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        //해당 상품의 이미지를 조회. 등록순으로 가지고오기 위해 상품 이미지 아이디 오름차순으로 가지고옴
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {//조회한 ItemImg 엔티티를 ItemImgDto객체로 만들어서 리스트에 추가
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId) //상품의 아이디를 통해 엔티티를 조회. 존재하지 않을때는 EntityNotFoundException을 발생시킴
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    //상품을 업데이트 할때도 마찬가지로 변경 감지 기능을 사용함
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId()) //1.상품 등록 화면으로부터 전달받은 상품 아이디를 이용하여 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto); //2.상품 등록 화면으로부터 전달받은 ItemFormDto 를 통해 상품 엔티티를 업데이트
        
        List<Long> itemImgIds = itemFormDto.getItemImgIds(); //3.상품 이미지 아이디 리스트를 조회

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i)); //4.상품 이미지를 업데이트하기 위해 updateItemImg() 메소드에 상품 이미지 아이디/ 상품이미지 파일정보를 파라미터로 전달
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    //메인 페이지를 보여줄 상품 데이터를 조회하는 메소드를 ItemService 클래스에 추가
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }


}