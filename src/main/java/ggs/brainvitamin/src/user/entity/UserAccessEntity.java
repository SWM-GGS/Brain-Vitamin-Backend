package ggs.brainvitamin.src.user.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "USER_ACCESS")
@NoArgsConstructor
public class UserAccessEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
}