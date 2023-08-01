package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.vitamin.entity.ProblemDetailEntity;
import ggs.brainvitamin.src.vitamin.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemDetailRepository extends JpaRepository<ProblemDetailEntity, Long> {

    ProblemDetailEntity findProblemDetailEntityByProblemAndAndDifficulty(ProblemEntity problemEntity, Integer difficulty);
}
