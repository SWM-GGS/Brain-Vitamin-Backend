package ggs.brainvitamin.src.post.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
import ggs.brainvitamin.src.user.entity.FamilyEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Table(name = "POST")
@NoArgsConstructor
@DynamicInsert
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
