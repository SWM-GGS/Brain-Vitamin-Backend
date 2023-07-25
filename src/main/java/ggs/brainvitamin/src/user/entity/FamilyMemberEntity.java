package ggs.brainvitamin.src.user.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Table(name = "FAMILY_MEMBER")
@NoArgsConstructor
@DynamicInsert
public class FamilyMemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(name = "family_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FamilyEntity family;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(nullable = false)
    private String relationship;
}
