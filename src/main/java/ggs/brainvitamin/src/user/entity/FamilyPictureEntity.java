package ggs.brainvitamin.src.user.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.Season;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Table(name = "FamilyPicture")
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class FamilyPictureEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false, length = 8)
    protected Season season = Season.SPRING;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false)
    private Integer headCount;

    public FamilyPictureEntity(UserEntity user, String imgUrl, Season season, Integer year, String place, Integer headCount) {
        this.user = user;
        this.imgUrl = imgUrl;
        this.season = season;
        this.year = year;
        this.place = place;
        this.headCount = headCount;
    }
}
