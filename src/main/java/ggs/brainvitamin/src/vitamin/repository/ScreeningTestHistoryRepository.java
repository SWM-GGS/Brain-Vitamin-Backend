package ggs.brainvitamin.src.vitamin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreeningTestHistoryRepository extends JpaRepository<ScreeningTestHistoryRepository, Long> {

}
