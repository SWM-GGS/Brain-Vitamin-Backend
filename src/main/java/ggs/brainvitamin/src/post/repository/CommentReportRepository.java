package ggs.brainvitamin.src.post.repository;

import ggs.brainvitamin.src.post.entity.CommentReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReportEntity, Long> {
}
