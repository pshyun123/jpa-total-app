package com.kh.jpatotalapp.service;

import com.kh.jpatotalapp.dto.BoardDto;
import com.kh.jpatotalapp.entity.Board;
import com.kh.jpatotalapp.entity.Category;
import com.kh.jpatotalapp.entity.Member;
import com.kh.jpatotalapp.repository.BoardRepository;
import com.kh.jpatotalapp.repository.CategoryRepository;
import com.kh.jpatotalapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    // 게시글 등록
    public boolean saveBoard(BoardDto boardDto) { // dto 파일에서 생성자를 가져옴?
        try {
            Board board = new Board(); // 새로운 Board객체 생성 /대문자 Board와 소문자 board의 차이?
            Member member = memberRepository.findByEmail(boardDto.getEmail()).orElseThrow(
                    () -> new RuntimeException("해당 회원이 존재하지 않습니다.")
            );
            Category category = categoryRepository.findById(boardDto.getCategoryId()).orElseThrow(
                    () -> new RuntimeException("해당 카테고리가 존재하지 않습니다.")
            );
            board.setTitle(boardDto.getTitle());
            board.setCategory(category);
            board.setContent(boardDto.getContent());
            board.setImgPath(boardDto.getImg());
            board.setMember(member);
            boardRepository.save(board);// 생성한 Board 객체를 저장
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    //게시글 전체 조회
    public List<BoardDto> getBoardList(){
        List<Board> boards = boardRepository.findAll();
        List<BoardDto> boardDtos = new ArrayList<>();
        for(Board board : boards){
            boardDtos.add(convertEntityToDto(board)); // 엔티티를 DTO로 변환하여 리스트에 추가
        }
        return boardDtos;
    }


    //게시글 상세 조회
    public BoardDto getBoardDetail(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new RuntimeException("해당 게시글이 존재하지 않습니다.")
        );
        return convertEntityToDto(board); // 엔티티를 DTO로 변환하여 반환

    }
//    public Board getBoardById(Long boardId) {
//        return boardRepository.findById(boardId)
//                .orElseThrow(() -> new RuntimeException("해당 ID의 게시글이 존재하지 않습니다."));
//    } -> 이 방법이랑 위의 방법의 다른점?


    //게시글 수정
    public boolean modifyBoard(Long id, BoardDto boardDto) {
        try {
            Board board = boardRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("해당 게시글이 존재하지 않습니다.")
            );
            board.setTitle(boardDto.getTitle());
            board.setContent(boardDto.getContent());
            board.setImgPath(boardDto.getImg());
            boardRepository.save(board);// 수정된 게시글 저장
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //게시글 삭제
    public boolean deleteBoard(Long id) {
        try {
            boardRepository.deleteById(id);// 해당 ID의 게시글 삭제
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //게시글 검색
    public List<BoardDto> searchBoard(String keyword) {
        List<Board> boards = boardRepository.findByTitleContaining(keyword);
        List<BoardDto> boardDtos = new ArrayList<>();
        for (Board board : boards) {
            boardDtos.add(convertEntityToDto(board)); // 엔티티를 DTO로 변환하여 리스트에 추가
        }
        return boardDtos;
    }
    //게시글 페이징 - 오버라이딩? 오버로딩?
    public List<BoardDto> getBoardList ( int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        List<Board> boards = boardRepository.findAll(pageable).getContent();// 페이징된 게시글 목록 조회
        List<BoardDto> boardDtos = new ArrayList<>();
        for (Board board : boards) {
            boardDtos.add(convertEntityToDto(board));
        }
        return boardDtos;
    }

    //게시글 엔티티를 DTO로 변환
    private BoardDto convertEntityToDto (Board board){
        BoardDto boardDto = new BoardDto();
        boardDto.setBoardId(board.getBoardId());
        boardDto.setTitle(board.getTitle());
        boardDto.setCategoryId(board.getCategory().getCategoryId());
        boardDto.setContent(board.getContent());
        boardDto.setImg(board.getImgPath());
        boardDto.setEmail(board.getMember().getEmail());
        boardDto.setRegDate(board.getRegDate());
        return boardDto;
    }
    //페이지 수 조회
    public int getBoards (Pageable pageable){
        return boardRepository.findAll(pageable).getTotalPages();
    }

}
