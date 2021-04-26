package io.cojam.web.dao;

import io.cojam.web.domain.Popup;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PopupDao {

    /**
     * 팝업 정보 저장
     * @param popup
     * @return
     */
    int savePopupInfo(Popup popup);

    /**
     * 팝업 목록
     * @param popup
     * @return
     */
    List<Popup> getPopupList(Popup popup);

    /**
     * 팝업 목록 CNT
     * @param popup
     * @return
     */
    Integer getPopupListCnt(Popup popup);

    /**
     * 팝업 상세 정보
     * @param popup
     * @return
     */
    Popup getPopupInfo(Popup popup);

    /**
     * 메인 팝업 리스트
     * @param popup
     * @return
     */
    List<Popup> getMainPopupList(Popup popup);

    /**
     * 팝업 정보 수정
     * @param popup
     * @return
     */
    int updatePopupInfo(Popup popup);

    int deletePopupInfo(Popup popup);


}
