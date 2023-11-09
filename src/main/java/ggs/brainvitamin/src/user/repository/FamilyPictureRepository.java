package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.src.user.entity.FamilyPictureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyPictureRepository extends JpaRepository<FamilyPictureEntity, Long> {

}
