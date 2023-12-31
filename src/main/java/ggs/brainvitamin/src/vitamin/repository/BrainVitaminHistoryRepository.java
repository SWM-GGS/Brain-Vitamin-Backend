package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.vitamin.entity.BrainVitaminHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrainVitaminHistoryRepository extends JpaRepository<BrainVitaminHistoryEntity, Long> {
}
