package ggs.brainvitamin.src.vitamin.entity;

import ggs.brainvitamin.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "problem")
@NoArgsConstructor
public class ProblemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProblemCategoryEntity problemCategory;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    private List<ProblemDetailEntity> problemDetailEntities = new ArrayList<>();

    @Column(nullable = false)
    private String trainingName;

    @Column(columnDefinition = "INT UNSIGNED NOT NULL")
    private Long timeLimit;

}
