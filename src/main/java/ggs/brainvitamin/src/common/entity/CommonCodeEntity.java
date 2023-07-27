package ggs.brainvitamin.src.common.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Table(name = "commonCode")
@Getter
public class CommonCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String codeName;

    @OneToMany(mappedBy = "commonCode", cascade = CascadeType.ALL)
    private List<CommonCodeDetailEntity> commonCodeDetailEntities;
}
