package ggs.brainvitamin.src.post.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "TAG")
@NoArgsConstructor
public class TagEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PostEntity post;

    @Column(nullable = false, columnDefinition = "char(10)")
    private String name;
}
