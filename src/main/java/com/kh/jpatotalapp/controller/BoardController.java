package com.kh.jpatotalapp.controller;


import com.kh.jpatotalapp.dto.BoardDto;
import com.kh.jpatotalapp.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kh.jpatotalapp.utils.Common.CORS_ORIGIN;

@Slf4j
//@CrossOrigin(origins = CORS_ORIGIN)// CrossOrigin은  다른 도메인에서의 HTTP 요청을 받아들일지를 설정하는 것
// 이를 통해 웹 애플리케이션이 클라이언트와 서버 간의 안전한 교환을 가능하게 함.
//CrossOrigin(*) 모든 요청에 접근 허용
// 여기서 주석처리 한 이유는 WebSecurityConfig에 값을 넣어주고 먼저 거쳐가기 때문에 필요가 없어졌기 때문
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    // 게시글 등록
    @PostMapping("/new")
    public ResponseEntity<Boolean> boardRegister(@RequestBody BoardDto boardDto){
        boolean isTrue = boardService.saveBoard(boardDto);
        return ResponseEntity.ok(isTrue);
    }
    // 게시글 수정
    @PutMapping("/modify/{id}")
    public ResponseEntity<Boolean> boardModify(@PathVariable Long id, @RequestBody BoardDto boardDto){
        boolean isTrue = boardService.modifyBoard(id, boardDto);
        return ResponseEntity.ok(isTrue);
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> boardDelete(@PathVariable Long id){//
        boolean isTrue = boardService.deleteBoard(id);
        return ResponseEntity.ok(isTrue);
    }

    // 게시글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<BoardDto>> boardList(){
        List<BoardDto> list = boardService.getBoardList();
        return ResponseEntity.ok(list);
    }

    // 게시글 목록 페이징
    @GetMapping("/list/page")
    public ResponseEntity<List<BoardDto>> boardList(@RequestParam(defaultValue = "0")int page,
                                                    @RequestParam(defaultValue = "20")int size){//.../page?page=1&size=5
        List<BoardDto> list = boardService.getBoardList(page, size);
        return ResponseEntity.ok(list);
    }

    // 게시글 상세 조회
    @GetMapping("/detail/{id}") // 해당값이 무슨의미로 들어갔는지 식별 불가, 어떤 요청이든 하나밖에 사용하지 못함, 주소가 바뀔 수 있으니..
    public ResponseEntity<BoardDto> boardDetail(@PathVariable Long id){
        BoardDto boardDto = boardService.getBoardDetail(id);
        return ResponseEntity.ok(boardDto);
    }

    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<List<BoardDto>> boardSearch(@RequestParam String keyword){ // 키워드를 식별가능-공공api에서 사용 많이 함, 한번에 여러개 쓰는 경우있음. get이랑 post 위주로 많이 사용.
        List<BoardDto> list = boardService.searchBoard(keyword);
        return ResponseEntity.ok(list);

    }

    // 페이지 수 조회
    @GetMapping("/count")
    public ResponseEntity<Integer> listBoards(@RequestParam(defaultValue = "0")int page,
                                             @RequestParam(defaultValue = "10")int size){
        PageRequest pageRequest = PageRequest.of(page,size);
        Integer pageCnt = boardService.getBoards(pageRequest);
        return ResponseEntity.ok(pageCnt);

    }
    // 회원 이메일로 게시글 조회
    @GetMapping("/list/email")
    public ResponseEntity<List<BoardDto>> boardListByEmail(@RequestParam String email) {
        List<BoardDto> list = boardService.getBoardListByEmail(email);
        return ResponseEntity.ok(list);
    }
}