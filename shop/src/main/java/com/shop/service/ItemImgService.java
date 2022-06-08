package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}") //@Value어노테이션으로 프로퍼티의 itemImgLocation 값을 itemImgLocation 변수에 대입
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes()); //사용자가 상품의 이미지를 등록했다면, 저장할 경로와 파일의 이름,
            //파일을 파일의 바이트 배열을 / 파일 업로드 파라미터로 uploadFile 메소드를 / 각각 호출 ??
             imgUrl = "/images/item/" + imgName; //저장된 상품 이미지를 불러올 경로 설정
            //외부리소스 불러오는 urlPatterns로 WebMvcOnfig 클래스에서 /images/** 로 설정했음. 또한
            //프로퍼티에서 설정한 uploadPath 프로퍼티 경로인 C:/shop/ 아래 item 폴더에 이미지를 저장하므로
            //상품 이미지를 불러오는 경로로 /images/item/ 를 붙여줌
        }

        //상품 이미지 정보 저장 imgName: 실제 로컬 저장된 상품 이미지 파일이름 / orilmgName : 업로드했던 상품 이미지 파일의 원래이름
        //imgUrl : 업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
        itemImg.updateItemImg(oriImgName, imgName, imgUrl); //입력받은 상품 이미지 정보 저장
        itemImgRepository.save(itemImg); //입력받은 상품 이미지 정보 저장
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation+"/"+
                        savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }



}