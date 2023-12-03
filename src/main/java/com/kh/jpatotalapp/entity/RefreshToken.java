package com.kh.jpatotalapp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name="refresh_token")
public class RefreshToken {
    @Id
    @Column(name="refresh_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="refresh_token")
    private String refreshToken;

    @Column(name="refresh_token_exp")
    private Long refreshTokenExpiresIn;

    // RefreshToken 엔터티와 Member 엔터티 간의 일대일 관계를 매핑
    // RefreshToken을 조회할 때 연관된 Member를 필요로 할 때만 로딩됨
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
}
