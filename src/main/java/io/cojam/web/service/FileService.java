package io.cojam.web.service;


import io.cojam.web.dao.FileDao;
import io.cojam.web.domain.FileInfo;
import io.cojam.web.domain.MyConfig;
import io.cojam.web.utils.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
