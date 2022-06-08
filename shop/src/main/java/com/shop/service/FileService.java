package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

//파일 처리하는 FileService 클래스
@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
        UUID uuid = UUID.randomUUID();
        //1.서로 다른 개체들을 구별하기 위해 이름 부여시, 사용
        //실제 사용 시, 중복될 가능성이 거의 없으므로 파일의 이름으로 사용하면 중복 문제 해결 가능
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        //2.UUID로 받은 값과 원래 파일의 이름의 확장자를 조합해서 저장될 파일 이름 만듦
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        //3. 바이트 단위의 출력을 내보내는 클래스. 생성자로 파일이 저장될 위치와 파일의 이름을 넘겨, 파일에 쓸 파일 출력 스트림 생성
        fos.write(fileData); //4. fileData를 파일 출력 스트림에 입력
        fos.close();
        return savedFileName; //5. 업로드된 파일의 이름을 반환
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath); //6.파일이 저장된 경로를 이용하여 파일 객체를 생성
        if(deleteFile.exists()) { //7.해당 파일이 존재하면 파일을 삭제
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }

}