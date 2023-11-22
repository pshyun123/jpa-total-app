package com.kh.jpatotalapp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "board")
@Getter @Setter @ToString
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long boardId; // 게시글 식별자
    private String title; // 제목
    private String content; // 내용
    private String imgPath; // 이미지 경로
    private LocalDateTime regDate; // 등록일
    @PrePersist // 데이터베이스에 엔터티를 저장하기 전에 실행되는 메소드
    public void prePersist(){
        regDate = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY) // Lazy 로딩 전략을 사용하여 카테고리를 지연 로딩-> 왜쓰는 거?
    @JoinColumn(name = "member_id") // Member 엔티티와의 외래 키 연결
    private Member member; // 작성자

    //카테고리 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // Category 엔티티와의 외래 키 연결
    private Category category;

    //Board와 Comment는 1:N 관계 mappedBy는 연관관계의 주인이 아니다(난 FK가 아니에요)라는 의미
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)//해당 게시글에 연결된 댓글이 삭제될 때 자동으로 삭제되도록 orphanRemoval 속성이 설정
    private List<Comment> comments; // 댓글 목록
}
//cascade = CascadeType.ALL와 orphanRemoval = true는 JPA에서 엔터티 간 관계를 관리하는 데 사용되는 지시어입니다.
//
//CascadeType.ALL:
//CascadeType.ALL은 부모 엔터티의 라이프사이클에 대해 자식 엔터티에게 모든 작업을 전파하는 옵션입니다.
//예를 들어, 만약 부모 엔터티가 저장될 때, 자식 엔터티도 함께 저장되어야 하거나, 부모 엔터티가 삭제될 때 자식 엔터티도 함께 삭제되어야 할 경우에 사용됩니다.
//orphanRemoval = true:
//orphanRemoval은 부모 엔터티에서 더 이상 참조되지 않는 자식 엔터티를 자동으로 삭제하는 옵션입니다.
//예를 들어, 부모 엔터티에서 자식 엔터티 목록을 관리하고 있을 때, 이 목록에서 자식 엔터티가 제거되면 자동으로 삭제되도록 지정할 수 있습니다.
//이 두 옵션을 함께 사용하는 경우, CascadeType.ALL은 부모 엔터티의 라이프사이클에 따라 자식 엔터티에게 작업을 전파하고, orphanRemoval은 부모 엔터티에서 제거된 자식 엔터티를 자동으로 삭제합니다. 이것은 특히 부모-자식 간의 일대다 또는 일대일 관계에서 유용합니다.
