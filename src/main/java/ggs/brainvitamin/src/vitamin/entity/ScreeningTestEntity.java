package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "screeningTest")
@NoArgsConstructor
public class ScreeningTestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_code")
    private CommonCodeDetailEntity areaCode;

    @Column(columnDefinition = "INT UNSIGNED default 0 NOT NULL")
    private Long parentId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer timeLimit;

    @Column()
    private String imgUrl;

    @Column()
    private String audioUrl;

    @Column(nullable = false)
    private String answer;

}
