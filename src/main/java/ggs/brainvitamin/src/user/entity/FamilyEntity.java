package ggs.brainvitamin.src.user.entity;

import ggs.brainvitamin.config.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Table(name = "FAMILY")
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class FamilyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(nullable = false, name = "family_key")
    private String familyKey;

    @Column(nullable = false, name = "member_count")
    private Integer memberCount;

    @Column(nullable = false, name = "family_name")
    private String familyName;

    @Column(nullable = false, name = "family_level")
    private Integer familyLevel;

    @Column(nullable = false, name = "family_exp")
    private Integer familyExp;


    public void increaseMemberCount() {
        this.memberCount++;
    }

    public void decreaseMemberCount() {
        this.memberCount--;
    }
}
