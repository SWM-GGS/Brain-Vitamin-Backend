package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.vitamin.entity.PoolMazeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolMazeRepository extends JpaRepository<PoolMazeEntity, Long> {

    @Query(value = "SELECT * FROM pool_maze where problem_id = :problemId and difficulty = :difficulty order by RAND() limit 1", nativeQuery = true)
    PoolMazeEntity findRandom1ByProblemAndDifficulty(@Param(value = "problemId") Long problemId, @Param(value = "difficulty") int difficulty);
}
