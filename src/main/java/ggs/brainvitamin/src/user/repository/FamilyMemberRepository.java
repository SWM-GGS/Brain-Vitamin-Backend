package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.src.user.entity.FamilyEntity;
import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMemberEntity, Long> {
    Optional<FamilyMemberEntity> findById(Long Id);
    List<FamilyMemberEntity> findByUserId(Long userId);

}
