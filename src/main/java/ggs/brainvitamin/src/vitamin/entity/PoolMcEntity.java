package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "poolMc")
@NoArgsConstructor
public class PoolMcEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private ProblemEntity problem;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false, columnDefinition = "CHAR(1)")
    private String answer;

    @Column(nullable = false)
    private String imgUrl;
}
