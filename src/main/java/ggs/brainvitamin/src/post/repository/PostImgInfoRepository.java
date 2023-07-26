package ggs.brainvitamin.src.post.repository;

import ggs.brainvitamin.src.post.entity.PostImgInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImgInfoRepository extends JpaRepository<PostImgInfoEntity, Long> {
}
