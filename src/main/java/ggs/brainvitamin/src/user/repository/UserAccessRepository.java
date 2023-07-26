package ggs.brainvitamin.src.user.repository;

import ggs.brainvitamin.src.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccessRepository extends JpaRepository<UserEntity, Long> {
}
