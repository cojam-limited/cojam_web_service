package io.cojam.web.service;


import io.cojam.web.constant.SnsCode;
import io.cojam.web.dao.FileDao;
import io.cojam.web.domain.FileInfo;
import io.cojam.web.domain.MyConfig;
import io.cojam.web.utils.CommonUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    FileDao fileDao;

    @Autowired
    MyConfig myConfig;



    @Cacheable(value = "fileKey" ,cacheManager = "userCacheManager")
    public FileInfo getFileInfo(String fileKey){
        return fileDao.getFileInfo(fileKey);
    }

    @Transactional
    public FileInfo fileUpload(String uplodUser, MultipartFile file, String folder, String mgmtNmbr) {
        FileInfo fileInfo = new FileInfo();

        try {
            fileInfo = fileUploadProcess(uplodUser, file, folder,mgmtNmbr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileInfo;
    }


    /**
     * 파일 업로드 프로세스
     * @param uplodUser
     * @param file
     * @param folder
     * @param mgmtNmbr
     * @return
     * @throws Exception
     */
    private FileInfo fileUploadProcess(String uplodUser, MultipartFile file, String folder,String mgmtNmbr) throws Exception {

        String realFolder = myConfig.getUploadPath();
        UUID uuid = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        extension = StringUtils.isBlank(extension)? "jpeg" : extension;

        if(extension.toUpperCase().equals("HEIC")){
            extension = "jpeg";
        }else if(extension.toUpperCase().equals("PLIST")){
            extension = "jpeg";
        }


        String realFileName = refineFileName(file.getOriginalFilename(), uuid.toString() + "." + extension);
        String newFileName = uuid.toString() + "_" + timestamp.getTime() + "." + extension;
        String filepathTxt = File.separator + folder + File.separator + mgmtNmbr + File.separator;
        String filepath = realFolder + filepathTxt;
        String filepathNm = filepath + newFileName;
        String fileKey = CommonUtils.getAuthCode(7) + timestamp.getTime() + CommonUtils.getAuthCode(3);
        File f = new File(filepathNm);
        FileInfo fileInfo = new FileInfo();
        if (!f.exists()) {
            fileInfo.setFileKey(fileKey);
            fileInfo.setTableKey(mgmtNmbr);
            fileInfo.setFileRealName(realFileName);
            fileInfo.setFileChangeName(newFileName);
            fileInfo.setFilePath(filepathTxt);
            fileInfo.setFileSize(file.getSize() + "");
            fileInfo.setFileExtension(extension);
            fileInfo.setCreateMemberKey(uplodUser);
            fileInfo.setUpdateMemberKey(uplodUser);
            fileDao.saveFileInfo(fileInfo);
            f.mkdirs();
        }
        file.transferTo(f);
        processFile(fileInfo, f);
        return fileInfo;
    }


    public FileInfo fileUrlUpload(String uplodUser, String snsUrl, String folder, String postMgmtNmbr) {

        FileInfo fileInfo = new FileInfo();
        try {

            URL url = null;
            BufferedImage bi = null;

            String realFolder = myConfig.getUploadPath();
            UUID uuid = UUID.randomUUID();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            String[] extensionArr = {"png", "jpg", "jpeg", "gif"};
            String extension = "jpg";
            for (String extensionStr : extensionArr) {
                if (StringUtils.lowerCase(snsUrl).indexOf("." + extensionStr) > -1) {
                    extension = extensionStr;
                    break;
                }
            }
            String str_filename = uuid.toString() + "_" + timestamp.getTime() + "." + extension;
            String org_filename = str_filename;
            String filepathTxt = File.separator + folder + File.separator + postMgmtNmbr + File.separator;
            String filepath = realFolder + filepathTxt;
            String filepathNm = filepath + str_filename;
            String fileKey = CommonUtils.getAuthCode(7) + timestamp.getTime() + CommonUtils.getAuthCode(3);
            File f = new File(filepathNm);

            if (!f.exists()) {
                f.mkdirs();
                url = new URL(snsUrl); // 다운로드 할 이미지 URL
                bi = ImageIO.read(url);
                if(ImageIO.write(bi, extension, f)) {
                    fileInfo.setFileKey(fileKey);
                    fileInfo.setTableKey(postMgmtNmbr);
                    fileInfo.setFileRealName(org_filename);
                    fileInfo.setFileChangeName(str_filename);
                    fileInfo.setFilePath(filepathTxt);
                    fileInfo.setFileSize(f.length() + "");
                    fileInfo.setFileExtension(extension);
                    fileInfo.setCreateMemberKey(uplodUser);
                    fileInfo.setUpdateMemberKey(uplodUser);
                    fileDao.saveFileInfo(fileInfo);
                }
            }

            processFile(fileInfo,f);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return fileInfo;
    }


    /**
     * URL 통해 다운로드 후 서버 저장(YOUTUBE)
     * @param uplodUser
     * @param youtubeId
     * @param folder
     * @param postMgmtNmbr
     * @return
     */
    public FileInfo fileUrlUploadYoutube(String uplodUser, String youtubeId, String folder,String postMgmtNmbr) {

        FileInfo fileInfo = new FileInfo();
        //유투브 썸네일 싸이즈 명
        String[] tumbNailArr={SnsCode.YOUTUBE_THNL_MAX , SnsCode.YOUTUBE_THNL_SDD, SnsCode.YOUTUBE_THNL_HQ , SnsCode.YOUTUBE_THNL_MQ  , SnsCode.YOUTUBE_THNL_DEFAULT};
        try {
            URL url = null;
            BufferedImage bi = null;
            String realFolder = myConfig.getUploadPath();
            UUID uuid = UUID.randomUUID();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String extension = "jpg";
            String str_filename = uuid.toString() + "_" + timestamp.getTime() + "." + extension;
            String org_filename = str_filename;
            String filepathTxt = File.separator + folder + File.separator + postMgmtNmbr + File.separator;
            String filepath = realFolder + filepathTxt;
            String filepathNm = filepath + str_filename;
            String fileKey = CommonUtils.getAuthCode(7) + timestamp.getTime() + CommonUtils.getAuthCode(3);
            File f = new File(filepathNm);
            if (!f.exists()) {
                f.mkdirs();
                for (String youtubeUrl : tumbNailArr){
                    url = new URL("https://img.youtube.com/vi/"+youtubeId+"/"+youtubeUrl); // 다운로드 할 이미지 URL
                    try {
                        bi = ImageIO.read(url);

                    }catch (Exception e){
                    }finally {
                        if(bi !=null){
                            break;
                        }
                    }
                }

                if(ImageIO.write(bi, extension, f)) {
                    fileInfo.setFileKey(fileKey);
                    fileInfo.setTableKey(postMgmtNmbr);
                    fileInfo.setFileRealName(org_filename);
                    fileInfo.setFileChangeName(str_filename);
                    fileInfo.setFilePath(filepathTxt);
                    fileInfo.setFileSize(f.length() + "");
                    fileInfo.setFileExtension(extension);
                    fileInfo.setCreateMemberKey(uplodUser);
                    fileInfo.setUpdateMemberKey(uplodUser);

                    //Youtube 일 경우 추가
                    BufferedImage image = ImageIO.read(f);

                    // 계산된 크기로 원본이미지를 가운데에서 crop 합니다.
                    if(image.getHeight()==480 || image.getHeight()==360 || image.getHeight()==90){
                        BufferedImage cropImg = null;

                        int ow = image.getWidth();
                        int oh = image.getHeight();
                        int nh = (int) (image.getHeight() - (image.getHeight()*0.25));

                        cropImg = Scalr.crop(image, 0, (oh-nh)/ 2, ow, nh);

                        ImageIO.write(cropImg, extension, f);
                    }
                    fileDao.saveFileInfo(fileInfo);
                    processFile(fileInfo, f);
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return fileInfo;
    }



    private String refineFileName(String fileName, String defaultName) {

        if (StringUtils.isBlank(fileName))
            return defaultName;

        String invalidCharRemoved = fileName.replaceAll("[\\\\/:*?\"<>|'\"]", "");

        if (StringUtils.isBlank(invalidCharRemoved))
            return defaultName;
        else
            return invalidCharRemoved;
    }

    public void processFile(FileInfo fileInfo, File originalFile) throws Exception {
        //adjustOrientation(fileInfo, originalFile);
    }

    public void deleteFileInfo(String filekey){
        FileInfo fileInfo = this.getFileInfo(filekey);
        if(fileInfo!=null && fileInfo.getDeleted()){
            String originalFilePath = getFilePath(fileInfo.getFilePath(),fileInfo.getFileChangeName());
            File file = new File(originalFilePath);
            if(file.delete()){
                fileDao.deleteFileInfo(fileInfo);
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
