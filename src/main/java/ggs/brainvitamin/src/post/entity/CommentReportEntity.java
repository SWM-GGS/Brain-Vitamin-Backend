package ggs.brainvitamin.src.post.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "COMMENT_REPORT")
@NoArgsConstructor
public class CommentReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(name = "comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommentEntity comment;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @JoinColumn(name = "report_type")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCodeDetailEntity reportTypeCode;

    @Column(nullable = false)
    private String contents;
}