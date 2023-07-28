package ggs.brainvitamin.src.common.repository;

import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.entity.CommonCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeDetailRepository extends JpaRepository<CommonCodeDetailEntity, Long> {
    CommonCodeDetailEntity findCommonCodeDetailEntityByCodeDetailName(String codeDetailName);
    List<CommonCodeDetailEntity> findByCommonCode(CommonCodeEntity commonCode);
}
