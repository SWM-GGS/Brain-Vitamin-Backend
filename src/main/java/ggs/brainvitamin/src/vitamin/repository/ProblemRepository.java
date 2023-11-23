package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.vitamin.entity.PoolSfEntity;
import ggs.brainvitamin.src.vitamin.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    @Query(value = "SELECT * FROM problem order by RAND() limit 8", nativeQuery = true)
    List<ProblemEntity> findProblemsRandom8();
}
