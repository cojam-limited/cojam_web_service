package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.dao.BoardDao;
import io.cojam.web.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Service
public class BoardService {

    @Autowired
    BoardDao boardDao;

    @Autowired
    SequenceService sequenceService;

    @Autowired
    FileService fileService;

    @Autowired
    PushMessageService pushMessageService;

    public List<Board> getHomeBoardList(Board board){
        return boardDao.getHomeBoardList(board);
    }

    public List<Board> getNoticeBoardList(Board board){
        return boardDao.getNoticeBoardList(board);
    }

    public Integer getNoticeBoardListCnt(Board board){
        return boardDao.getNoticeBoardListCnt(board);
    }

    public List<BoardCategory> getNoticeCategoryList(){
        return boardDao.getNoticeCategoryList();
    }

    public Board getNoticeBoardDetail(Board board){
        return boardDao.getNoticeBoardDetail(board);
    }

    public List<Board> getNoticeRelatedList(Board board){
        return boardDao.getNoticeRelatedList(board);
    }

    @Transactional
    public ResponseDataDTO saveBoard(Board board, MultipartFile file, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        board.setCreateMemberKey(account.getMemberKey());
        board.setUpdateMemberKey(account.getMemberKey());
        //보드 시퀀스 채번
        board.setBoardKey(sequenceService.getSequence(SequenceCode.TB_BOARD));
        //파일 업로드
        FileInfo fileInfo =fileService.fileUpload(account.getMemberKey(),file, SequenceCode.TB_BOARD,board.getBoardKey());
        if(fileInfo!= null && fileInfo.getFileKey()!=null){
            board.setBoardFile(fileInfo.getFileKey());
        }
        boardDao.saveBoard(board);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(board.getBoardKey());
        return responseDataDTO;
    }


    @Transactional
    public ResponseDataDTO updateBoard(Board board, MultipartFile file, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        board.setCreateMemberKey(account.getMemberKey());
        board.setUpdateMemberKey(account.getMemberKey());

        Board detail = boardDao.getNoticeBoardDetail(board);
        if(detail==null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("no data.");
        }


        Boolean isFileDel = false;
        if(file != null){
            //파일 업로드
            FileInfo fileInfo =fileService.fileUpload(account.getMemberKey(),file, SequenceCode.TB_BOARD,board.getBoardKey());
            if(fileInfo!= null && fileInfo.getFileKey()!=null){
                board.setBoardFile(fileInfo.getFileKey());
                isFileDel=true;
            }
        }
        boardDao.updateBoard(board);

        if(isFileDel){
            //파일 정보 삭제
            fileService.deleteFileInfo(detail.getBoardFile());
        }

        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(board.getBoardKey());
        return responseDataDTO;
    }

    @Transactional
    public ResponseDataDTO deleteBoard(Board board, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        board.setCreateMemberKey(account.getMemberKey());
        board.setUpdateMemberKey(account.getMemberKey());

        Board detail = boardDao.getNoticeBoardDetail(board);
        if(detail==null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("no data.");
        }

        fileService.deleteFileInfo(detail.getBoardFile());
        boardDao.deleteBoard(board);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(board.getBoardKey());
        return responseDataDTO;
    }

    public ResponseDataDTO sendBoard(Board board, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        pushMessageService.sendPushMessage("NOTICE",board.getBoardKey());
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(board.getBoardKey());
        return responseDataDTO;
    }
}
