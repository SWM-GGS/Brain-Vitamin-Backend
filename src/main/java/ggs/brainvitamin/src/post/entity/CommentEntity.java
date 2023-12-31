package ggs.brainvitamin.src.post.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "COMMENT")
@NoArgsConstructor
public class CommentEntity extends BaseEntity {

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

    @Column(nullable = false)
    private String contents;

    @Column(name = "parents_id", columnDefinition = "INT UNSIGNED default 0 NOT NULL")
    private Long parentsId;
}

