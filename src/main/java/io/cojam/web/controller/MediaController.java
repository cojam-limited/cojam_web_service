package io.cojam.web.controller;

import io.cojam.web.domain.FileInfo;
import io.cojam.web.domain.MyConfig;
import io.cojam.web.service.FileService;
import io.cojam.web.utils.MultipartFileSender;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.concurrent.TimeUnit;

@Controller
public class MediaController {

    @Autowired
    MyConfig myConfig;

    @Autowired
    FileService fileService;

    /**
     * 아이디로 이미지 호출
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/media/image", method = RequestMethod.GET)
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

    /**
     * 아이디로 비디오 호출
     * @param req
     * @param res
     * @param id
     */
    @RequestMapping(value = "/user/media/video/{id}", method = RequestMethod.GET)
    public void getVideo(HttpServletRequest req, HttpServletResponse res, @PathVariable String id) {
        FileInfo fileInfo = fileService.getFileInfo(id);

        File getFile =  new File(getFilePath(fileInfo.getFilePath(),fileInfo.getFileChangeName()));
        res.setContentType("video/mp4");
        try {
            // 미디어 처리
            MultipartFileSender
                    .fromFile(getFile)
                    .with(req)
                    .with(res)
                    .serveResource();

        } catch (Exception e) {
            // 사용자 취소 Exception 은 콘솔 출력 제외
            if (!e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException")) e.printStackTrace();
        }
    }

    public String getFilePath(String fileCurs,String fileName) {
        String filePath = "";
        String uploadPath = myConfig.getUploadPath();
        filePath = uploadPath + fileCurs + fileName;
        return filePath;
    }
}
