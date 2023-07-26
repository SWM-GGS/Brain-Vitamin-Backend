package ggs.brainvitamin.src.post.repository;

import ggs.brainvitamin.src.post.entity.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
}
