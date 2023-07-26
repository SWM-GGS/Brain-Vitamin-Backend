package ggs.brainvitamin.src.vitamin.repository;

import ggs.brainvitamin.src.vitamin.entity.PoolMcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolMcRepository extends JpaRepository<PoolMcEntity, Long> {
}
