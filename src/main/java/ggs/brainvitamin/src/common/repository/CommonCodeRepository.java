package ggs.brainvitamin.src.common.repository;

import ggs.brainvitamin.src.common.entity.CommonCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCodeEntity, Long> {
}
