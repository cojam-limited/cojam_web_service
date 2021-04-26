package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.dao.PopupDao;
import io.cojam.web.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PopupService {

    @Autowired
    PopupDao popupDao;

    @Autowired
    SequenceService sequenceService;

    @Autowired
    FileService fileService;

    @Transactional
    public ResponseDataDTO savePopup(Popup popup, MultipartFile file, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        popup.setCreateMemberKey(account.getMemberKey());
        popup.setUpdateMemberKey(account.getMemberKey());
        //팝업 시퀀스 채번
        popup.setPopupKey(sequenceService.getSequence(SequenceCode.TB_POPUP));
        //파일 업로드
        FileInfo fileInfo =fileService.fileUpload(account.getMemberKey(),file, SequenceCode.TB_POPUP,popup.getPopupKey());
        if(fileInfo!= null && fileInfo.getFileKey()!=null){
            popup.setPopupFileKey(fileInfo.getFileKey());
        }

        popupDao.savePopupInfo(popup);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(popup.getPopupKey());
        return responseDataDTO;
    }

    public List<Popup> getPopupList(Popup popup){
        return popupDao.getPopupList(popup);
    }

    public Integer getPopupListCnt(Popup popup){
        return popupDao.getPopupListCnt(popup);
    }

    public Popup getPopupInfo(Popup popup){
        return popupDao.getPopupInfo(popup);
    }

    public List<Popup> getMainPopupList(Popup popup){
        return popupDao.getMainPopupList(popup);
    }

    @Transactional
    public ResponseDataDTO updatePopup(Popup popup, MultipartFile file, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        popup.setUpdateMemberKey(account.getMemberKey());

        Popup detail = popupDao.getPopupInfo(popup);
        if(detail==null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("no data.");
            return responseDataDTO;
        }


        Boolean isFileDel = false;
        if(file != null){
            //파일 업로드
            FileInfo fileInfo =fileService.fileUpload(account.getMemberKey(),file, SequenceCode.TB_POPUP,popup.getPopupKey());
            if(fileInfo!= null && fileInfo.getFileKey()!=null){
                popup.setPopupFileKey(fileInfo.getFileKey());
                isFileDel=true;
            }
        }
        popupDao.updatePopupInfo(popup);


        if(isFileDel){
            //파일 정보 삭제
            fileService.deleteFileInfo(detail.getPopupFileKey());
        }

        popupDao.updatePopupInfo(popup);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(popup.getPopupKey());
        return responseDataDTO;
    }


    @Transactional
    public ResponseDataDTO deletePopup(String popupKey,Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();

        Popup popup = new Popup();
        popup.setPopupKey(popupKey);
        Popup detail = popupDao.getPopupInfo(popup);
        if(detail==null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("no data.");
        }


        fileService.deleteFileInfo(detail.getPopupFileKey());

        popupDao.deletePopupInfo(popup);

        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(popup.getPopupKey());
        return responseDataDTO;
    }
}
