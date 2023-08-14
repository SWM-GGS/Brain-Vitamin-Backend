package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.vitamin.entity.VitaminAnalyticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VitaminAnalyticsRepository extends JpaRepository<VitaminAnalyticsEntity, Long> {

    List<VitaminAnalyticsEntity> findTop5ByUserOrderByCreatedAtDesc(UserEntity userEntity);

    Optional<VitaminAnalyticsEntity> findTop1ByUserAndFinishOrderByCreatedAtDesc(UserEntity userEntity, String finish);
    List<VitaminAnalyticsEntity> findByUserAndFinishAndStatusAndCreatedAtBetweenOrderByCreatedAt(UserEntity user,
                                                                                                 String finish,
                                                                                                 Status status,
                                                                                                 LocalDateTime startDate,
                                                                                                 LocalDateTime endDate);

    List<VitaminAnalyticsEntity> findByUserAndStatusAndCreatedAtBetweenOrderByCreatedAt(UserEntity user,
                                                                                                 Status status,
                                                                                                 LocalDateTime startDate,
                                                                                                 LocalDateTime endDate);

}
