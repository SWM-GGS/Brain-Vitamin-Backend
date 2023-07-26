package ggs.brainvitamin.src.post.repository;

import ggs.brainvitamin.src.post.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
}
