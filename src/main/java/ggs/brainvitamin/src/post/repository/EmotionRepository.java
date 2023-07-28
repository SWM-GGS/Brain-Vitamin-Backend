package ggs.brainvitamin.src.post.repository;

import ggs.brainvitamin.src.post.entity.CommentReportEntity;
import ggs.brainvitamin.src.post.entity.EmotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmotionRepository extends JpaRepository<EmotionEntity, Long> {

    Optional<EmotionEntity> findById(Long id);
}
