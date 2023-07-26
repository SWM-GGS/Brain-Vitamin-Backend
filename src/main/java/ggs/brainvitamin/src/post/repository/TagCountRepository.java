package ggs.brainvitamin.src.post.repository;

import ggs.brainvitamin.src.post.entity.TagCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagCountRepository extends JpaRepository<TagCountEntity, Long> {
}
