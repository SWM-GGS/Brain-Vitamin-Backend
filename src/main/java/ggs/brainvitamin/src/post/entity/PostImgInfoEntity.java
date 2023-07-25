package ggs.brainvitamin.src.post.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "POST_IMG_INFO")
@NoArgsConstructor
public class PostImgInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(name = "img_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PostImgEntity postImg;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column()
    private LocalDate year;

    @Column()
    private String season;

    @Column()
    private String place;
}
