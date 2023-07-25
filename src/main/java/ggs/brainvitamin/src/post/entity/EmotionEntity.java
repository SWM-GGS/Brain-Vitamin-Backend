package ggs.brainvitamin.src.post.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "EMOTION")
@NoArgsConstructor
public class EmotionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PostEntity post;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @JoinColumn(name = "emotion_type")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCodeDetailEntity emotionTypeCode;
}
