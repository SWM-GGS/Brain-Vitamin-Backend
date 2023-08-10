package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.FamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<FamilyEntity, Long> {
    Optional<FamilyEntity> findByIdAndStatus(Long familyId, Status status);
    Optional<FamilyEntity> findByFamilyKeyAndStatus(String familyKey, Status status);
}
