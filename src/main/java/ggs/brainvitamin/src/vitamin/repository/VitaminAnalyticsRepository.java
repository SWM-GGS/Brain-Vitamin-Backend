package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.vitamin.entity.VitaminAnalyticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitaminAnalyticsRepository extends JpaRepository<VitaminAnalyticsEntity, Long> {

    List<VitaminAnalyticsEntity> findTop5ByUserOrderByCreatedAtDesc(UserEntity userEntity);

}
