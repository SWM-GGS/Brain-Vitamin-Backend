package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.FamilyEntity;
import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMemberEntity, Long> {
    Optional<FamilyMemberEntity> findById(Long Id);
    List<FamilyMemberEntity> findByUserIdAndStatus(Long userId, Status status);
    List<FamilyMemberEntity> findTop2ByFamilyId(Long familyId);
    Optional<FamilyMemberEntity> findByUserIdAndFamilyIdAndStatus(Long userId, Long familyId, Status status);

    List<FamilyMemberEntity> findByFamilyIdAndStatus(Long familyId, Status status);

}
