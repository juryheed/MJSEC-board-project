package org.mjulikelion.board.repository;

import org.mjulikelion.board.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    //username존재 여부 반환
    Boolean existsByUsername(String username);
    //Username으로 유저 찾기
    UserEntity findByUsername(String username);
}
