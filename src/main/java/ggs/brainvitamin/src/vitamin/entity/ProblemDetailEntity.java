package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "problemDetail")
@NoArgsConstructor
public class ProblemDetailEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(columnDefinition = "TINYINT NOT NULL")
    private Integer difficulty;

    @Column(columnDefinition = "TINYINT NOT NULL")
    private Integer elementSize;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String poolLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private ProblemEntity problem;

}
