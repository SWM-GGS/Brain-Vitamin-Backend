package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.vitamin.entity.PoolMcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoolMcRepository extends JpaRepository<PoolMcEntity, Long> {

    @Query(value = "SELECT * FROM pool_mc where problem_id = :problemId order by RAND() limit 8", nativeQuery = true)
    List<PoolMcEntity> findRandom8ByProblem(@Param(value = "problemId") Long problemId);

    @Query(value = "SELECT * FROM pool_mc order by RAND() limit 10", nativeQuery = true)
    List<PoolMcEntity> findRandom10();
}
