package io.cojam.web.dao;

import io.cojam.web.domain.Board;
import io.cojam.web.domain.BoardCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BoardDao {

    /**
     * 메인 게시글 목록
     * @param board
     * @return
     */
    List<Board> getHomeBoardList(Board board);

    /**
     * Notice List
     * @param board
     * @return
     */
    List<Board> getNoticeBoardList(Board board);

    /**
     * Notice List Cnt
     * @param board
     * @return
     */
    Integer getNoticeBoardListCnt(Board board);

    /**
     * 게시글 카테고리 목록
     * @return
     */
    List<BoardCategory> getNoticeCategoryList();

    /**
     * 게시글 상세
     * @param board
     * @return
     */
    Board getNoticeBoardDetail(Board board);

    /**
     * 게시글 최근 목록
     * @param board
     * @return
     */
    List<Board> getNoticeRelatedList(Board board);

    /**
     * 게시글 저장
     * @param board
     * @return
     */
    int saveBoard(Board board);

    /**
     * 게시글 수정
     * @param board
     * @return
     */
    int updateBoard(Board board);

    /**
     * 게시글 삭제
     * @param board
     * @return
     */
    int deleteBoard(Board board);

    /**
     * 게시글 카테고리 등록
     * @param boardCategory
     * @return
     */
    int saveBoardCategory(BoardCategory boardCategory);

    /**
     * 게시글 카테고리 수정
     * @param boardCategory
     * @return
     */
    int updateBoardCategory(BoardCategory boardCategory);

    /**
     * 게시글 카테고리 삭제
     * @param boardCategory
     * @return
     */
    int deleteBoardCategory(BoardCategory boardCategory);
}
