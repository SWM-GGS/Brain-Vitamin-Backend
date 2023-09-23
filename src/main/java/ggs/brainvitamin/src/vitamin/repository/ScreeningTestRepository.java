package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.vitamin.entity.ScreeningTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningTestRepository extends JpaRepository<ScreeningTestEntity, Long> {
    List<ScreeningTestEntity> findAllByStatus(Status status);
}
