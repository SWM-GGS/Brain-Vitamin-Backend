package ggs.brainvitamin.src.common.repository;

import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommonCodeDetailEntity, Long> {
}
