package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "vitaminAnalytics")
@NoArgsConstructor
public class VitaminAnalyticsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private Integer memoryScore;

    @Column
    private Integer attentionScore;

    @Column
    private Integer orientationScore;

    @Column
    private Integer visualScore;

    @Column
    private Integer languageScore;

    @Column
    private Integer calculationScore;

    @Column
    private Integer executiveScore;

    @Column
    private Integer soundScore;
}
