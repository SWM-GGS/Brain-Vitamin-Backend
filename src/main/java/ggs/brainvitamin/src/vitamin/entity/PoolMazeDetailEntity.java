package ggs.brainvitamin.src.vitamin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "pool_MAZE_detail")
@NoArgsConstructor
public class PoolMazeDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maze_id")
    private PoolMazeEntity poolMaze;

    @Column(nullable = false)
    private Float x;

    @Column(nullable = false)
    private Float y;

    @Column(nullable = false, columnDefinition = "CHAR(1)")
    private String answer;
}
