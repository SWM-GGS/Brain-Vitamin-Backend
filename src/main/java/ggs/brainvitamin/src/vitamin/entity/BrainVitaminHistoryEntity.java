package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "brainVitaminHistory")
@NoArgsConstructor
public class BrainVitaminHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private ProblemEntity problem;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Float duration;

    @Column(nullable = false)
    private String result;

    @Builder
    public BrainVitaminHistoryEntity(UserEntity user, ProblemEntity problem, Integer score, Float duration, String result) {
        this.user = user;
        this.problem = problem;
        this.score = score;
        this.duration = duration;
        this.result = result;
    }
}
