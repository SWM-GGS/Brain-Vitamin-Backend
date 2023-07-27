package ggs.brainvitamin.src.post.repository;

import ggs.brainvitamin.src.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Optional<PostEntity> findById(Long Id);
    List<PostEntity> findByFamilyIdOrderByCreatedAtDesc(Long familyId);
}
