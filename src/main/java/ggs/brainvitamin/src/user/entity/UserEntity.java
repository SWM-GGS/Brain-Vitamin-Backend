package ggs.brainvitamin.src.user.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "USER")
@NoArgsConstructor
@DynamicInsert
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @JoinColumn(nullable = false, name = "user_type")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCodeDetailEntity userTypeCode;

    @JoinColumn(name = "education")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCodeDetailEntity educationCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = true, name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = true)
    private String gender;

    @Column(nullable = false, name = "font_size")
    private Integer fontSize;

    @Column(nullable = true, name = "profile_img")
    private String profileImgUrl;

    @Column(columnDefinition = "INT UNSIGNED default 0 NOT NULL")
    private Integer consecutiveDays;
}
