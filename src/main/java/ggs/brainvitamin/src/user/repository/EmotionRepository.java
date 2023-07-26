package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.src.post.entity.CommentReportEntity;
import ggs.brainvitamin.src.post.entity.EmotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRepository extends JpaRepository<EmotionEntity, Long> {
}
