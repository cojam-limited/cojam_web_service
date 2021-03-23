package io.cojam.web.controller;

import io.cojam.web.domain.FileInfo;
import io.cojam.web.domain.MyConfig;
import io.cojam.web.service.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(value = "/user/media/")
public class MediaController {

    @Autowired
    MyConfig myConfig;

    @Autowired
    FileService fileService;

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getAtcm(
            String id
    ) throws Exception {

        byte[] imageData = null;
        String fileName="";
        try {
            if(!StringUtils.isBlank(id)){
                FileInfo fileInfo = fileService.getFileInfo(id);
                if(fileInfo != null){
                    fileName = fileInfo.getFileChangeName();
                    String originalFilePath = getFilePath(fileInfo.getFilePath(),fileInfo.getFileChangeName());
                    File imgFile = new File(originalFilePath);
                    if (imgFile.exists()) {
                        imageData = FileUtils.readFileToByteArray(imgFile);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (imageData == null || imageData.length == 0) {

                String originalFilePath = getFilePath("/default/", "noImage.jpeg");
                File imgFile = new File(originalFilePath);
                if (imgFile.exists()) {
                    imageData = FileUtils.readFileToByteArray(imgFile);
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" +
                                fileName +
                                "\"")
                        .cacheControl(CacheControl.noCache())
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageData);

            } else {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" +
                                fileName +
                                "\"")
                        .header(HttpHeaders.CACHE_CONTROL, "max-age=" + 31536000)
                        .cacheControl(CacheControl.maxAge(31536000, TimeUnit.HOURS))
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageData);
            }
        }

    }

    public String getFilePath(String fileCurs,String fileName) {
        String filePath = "";
        String uploadPath = myConfig.getUploadPath();
        filePath = uploadPath + fileCurs + fileName;
        return filePath;
    }
}
