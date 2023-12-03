package com.kh.jpatotalapp.entity;
import com.kh.jpatotalapp.constant.Authority;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "member")
@Getter @Setter @ToString
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String password;
    @Column(unique = true)
    private String email;
    private String image;
    private LocalDateTime regDate;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    // prePersist 메서드: 엔터티가 영속화(Persist)되기 전 호출되는 메서드. DB에 저장 전에 실행되어야 하는 로직 정의
    // 현재 시간을 얻어와서 regDate라는 필드에 할당 / DB에 레코드가 추가 -> 해당 필드에 현재 시간이 자동으로 기록(자동갱신)
    @PrePersist
    public void prePersist() {
        regDate = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Board> boards;


    //Member와 RefreshToken 간 일대일 양방향 관계. RefreshToken이 Member에 속하고 Member 엔터티의 생명주기에 영향을 받음
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;
    //mappedBy = "member": 이 관계의 주인(owning side)이 member 필드
    //Member 엔터티의 변경이 발생하면, 이와 연관된 RefreshToken 엔터티에도 동일한 변경이 전파됨
    //Member 엔터티의 refreshToken이 null로 설정되면 해당 RefreshToken이 자동으로 삭제

    @Builder // 빌더 패턴 적용
    public Member(String name, String password, String email, String image, Authority authority) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.image = image;
        this.authority = authority;
        this.regDate = LocalDateTime.now();
    }
}