package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "poolMaze")
@NoArgsConstructor
public class PoolMazeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private ProblemEntity problem;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private Integer difficulty;

    @OneToMany(mappedBy = "poolMaze", cascade = CascadeType.ALL)
    private List<PoolMazeDetailEntity> poolMazeDetails = new ArrayList<>();

}
