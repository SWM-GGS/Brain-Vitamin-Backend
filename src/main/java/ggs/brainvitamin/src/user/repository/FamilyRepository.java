package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.src.user.entity.FamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends JpaRepository<FamilyEntity, Long> {

}
