package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = "authorities")
    Optional<UserEntity> findOneWithAuthoritiesByPhoneNumberAndStatus(String phoneNumber, Status status);
    Optional<UserEntity> findByIdAndStatus(Long userId, Status status);
    Optional<UserEntity> findByPhoneNumberAndStatus(String phoneNumber, Status status);
    Optional<UserEntity> findByNicknameAndStatus(String nickname, Status status);
    List<UserEntity> findAllByUserTypeCodeAndStatus(CommonCodeDetailEntity commonCodeDetailEntity, Status status);
}
