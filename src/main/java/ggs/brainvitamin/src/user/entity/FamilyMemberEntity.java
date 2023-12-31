package ggs.brainvitamin.src.user.entity;

import ggs.brainvitamin.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
@Table(name = "FAMILY_MEMBER")
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class FamilyMemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(nullable = false, name = "family_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FamilyEntity family;

    @JoinColumn(nullable = false, name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(nullable = false)
    private String relationship;

    @Column
    private String familyGroupProfileImg;
}
