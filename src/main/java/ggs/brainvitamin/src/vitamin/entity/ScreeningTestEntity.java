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

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false, columnDefinition = "CHAR(1)")
    private String answer;

}
