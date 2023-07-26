package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMemberEntity, Long> {

}
