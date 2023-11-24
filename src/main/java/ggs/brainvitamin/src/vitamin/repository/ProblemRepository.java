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


    @Query(value = "select p.* from problem p join problem_category pc on p.category_id = pc.id where pc.area_code = 20 and p.status = 'ACTIVE' order by rand() limit 1", nativeQuery = true)
    ProblemEntity findMemoryProblemsRandom();

    @Query(value = "select p.* from problem p join problem_category pc on p.category_id = pc.id where pc.area_code = 21 and p.status = 'ACTIVE' order by rand() limit 2", nativeQuery = true)
    List<ProblemEntity> findAttentionProblemsRandom();

    @Query(value = "select p.* from problem p join problem_category pc on p.category_id = pc.id where pc.area_code = 22 and p.status = 'ACTIVE' order by rand() limit 1", nativeQuery = true)
    ProblemEntity findVisualProblemsRandom();

    @Query(value = "select p.* from problem p join problem_category pc on p.category_id = pc.id where pc.area_code = 23 and p.status = 'ACTIVE' order by rand() limit 1", nativeQuery = true)
    ProblemEntity findLanguageProblemsRandom();

    @Query(value = "select p.* from problem p join problem_category pc on p.category_id = pc.id where pc.area_code = 24 and p.status = 'ACTIVE' order by rand() limit 1", nativeQuery = true)
    ProblemEntity findCalculateProblemsRandom();

    @Query(value = "select p.* from problem p join problem_category pc on p.category_id = pc.id where pc.area_code = 25 and p.status = 'ACTIVE' order by rand() limit 1", nativeQuery = true)
    ProblemEntity findExecutiveProblemsRandom();

    @Query(value = "select p.* from problem p join problem_category pc on p.category_id = pc.id where pc.area_code = 26 and p.status = 'ACTIVE'order by rand() limit 1", nativeQuery = true)
    ProblemEntity findOrientationProblemsRandom();


}
