package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.vitamin.entity.PoolCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoolCardRepository extends JpaRepository<PoolCardEntity, Long> {
    @Query(value = "SELECT * FROM pool_card pc where pc.problem_id = :problemId order by RAND() limit 6", nativeQuery = true)
    List<PoolCardEntity> findRandom6ByProblem(@Param(value = "problemId") Long problemId);
}
