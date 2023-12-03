package com.kh.jpatotalapp.repository;

import com.kh.jpatotalapp.entity.Member;
import com.kh.jpatotalapp.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByRefreshToken(String refreshToken);
    boolean existsByMember(Member member);
    Optional<RefreshToken> findByMember(Member member);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.member = :member")
    void deleteByMember(@Param("member") Member member);
}
