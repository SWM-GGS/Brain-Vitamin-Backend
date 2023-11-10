package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.FamilyPictureEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyPictureRepository extends JpaRepository<FamilyPictureEntity, Long> {

    List<FamilyPictureEntity> findAllByUserAndStatus(UserEntity userEntity, Status status);
}
