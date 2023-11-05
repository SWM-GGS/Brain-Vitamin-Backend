package ggs.brainvitamin.src.post.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.post.patient.dto.EmotionDto;
import ggs.brainvitamin.src.user.entity.FamilyEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "POST")
@NoArgsConstructor
@DynamicInsert
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @JoinColumn(name = "post_type")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCodeDetailEntity postTypeCode;

    @JoinColumn(name = "family_id", columnDefinition = "INT UNSIGNED default 0 NOT NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    private FamilyEntity family;

    @Column(nullable = false, columnDefinition = "varchar(500)")
    private String contents;

    @Column(nullable = false)
    private String viewers;

    @Column(nullable = false, name = "emotions_count")
    private Long emotionsCount;

    @Column(nullable = false, name = "comments_count")
    private Long commentsCount;

    @Column(nullable = false, name = "viewers_count")
    private Long viewersCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImgEntity> postImgEntityList;

    @OneToMany(mappedBy = "post")
    private List<CommentEntity> commentEntityList;

    @OneToMany(mappedBy = "post")
    @OrderBy("createdAt asc")
    private List<EmotionEntity> emotionEntityList;

    public void increaseEmotionsCount() {
        this.emotionsCount++;
    }

    public void decreaseEmotionCount() {
        this.emotionsCount--;
    }
}
