package ggs.brainvitamin.src.common.repository;

import ggs.brainvitamin.src.common.entity.CommonCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCodeEntity, Long> {

    Optional<CommonCodeEntity> findByCode(String code);
}
