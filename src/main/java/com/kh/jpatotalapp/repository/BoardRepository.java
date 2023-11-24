package com.kh.jpatotalapp.repository;
//Repository: 데이터베이스와 관련된 작업을 처리하기 위한 스프링 빈으로 선언되는 애노테이션, 안해도 상관 없나?

import com.kh.jpatotalapp.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
import java.util.List;

//@Repository
public interface BoardRepository extends JpaRepository<Board, Long> { // 꼭 인터페이스로!!!
    List<Board> findByTitleContaining(String keword); // 제목에 특정 키워드를 포함하는 게시글을 찾기 위한 메소드를 선언
    Page<Board> findAll(Pageable pageable); //페이징 처리된 모든 게시글을 가져오기 위한 메소드,Pageable은 페이징 및 정렬 관련 정보를 전달 위한 객체
    List<Board> findByMemberEmail(String email);

}
