package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.vitamin.entity.ScreeningTestHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScreeningTestHistoryRepository extends JpaRepository<ScreeningTestHistoryEntity, Long> {
    Optional<ScreeningTestHistoryEntity> findTop1ByUserAndStatusOrderByCreatedAtDesc(UserEntity userEntity, Status status);
}
