package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.vitamin.entity.PoolSfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoolSfRepository extends JpaRepository<PoolSfEntity, Long> {
    @Query(value = "SELECT * FROM pool_sf where problem_id = :problemId order by RAND() limit 3", nativeQuery = true)
    List<PoolSfEntity> findRandom3ByProblem(@Param(value = "problemId") Long problemId);

    @Query(value = "SELECT * FROM pool_sf order by RAND() limit :elementSize", nativeQuery = true)
    List<PoolSfEntity> findRandomN(@Param(value = "elementSize") Integer problemId);
}
