package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "pool_SF")
@NoArgsConstructor
public class PoolSfEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private ProblemEntity problem;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String elementName;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private Integer minRange;

    @Column(nullable = false)
    private Integer maxRange;

}
