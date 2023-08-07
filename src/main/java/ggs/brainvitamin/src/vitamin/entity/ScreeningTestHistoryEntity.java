package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "screeningTestHistory")
@NoArgsConstructor
public class ScreeningTestHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false)
    private Integer orientationScore;

    @Column(nullable = false)
    private Integer attentionScore;

    @Column(nullable = false)
    private Integer spaceTimeScore;

    @Column(nullable = false)
    private Integer executiveScore;

    @Column(nullable = false)
    private Integer memoryScore;

    @Column(nullable = false)
    private Integer languageScore;

    public ScreeningTestHistoryEntity(UserEntity user, Integer totalScore) {
        this.user = user;
        this.totalScore = totalScore;
        this.orientationScore = 0;
        this.attentionScore = 0;
        this.spaceTimeScore = 0;
        this.executiveScore = 0;
        this.memoryScore = 0;
        this.languageScore = 0;
    }
}
