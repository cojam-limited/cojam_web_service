package io.cojam.web.dao;

import io.cojam.web.domain.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FileDao {

    /**
     * 파일 정보 가져오기
     * @param fileKey
     * @return
     */
    FileInfo getFileInfo(String fileKey);

    /**
     * 파일 정보 저장
     * @param fileInfo
     * @return
     */
    int saveFileInfo(FileInfo fileInfo);

    /**
     * 파일 정보 삭제
     * @param fileInfo
     * @return
     */
    int deleteFileInfo(FileInfo fileInfo);
}
