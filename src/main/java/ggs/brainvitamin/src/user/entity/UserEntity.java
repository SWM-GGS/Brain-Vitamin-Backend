package ggs.brainvitamin.src.user.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
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

    @JoinColumn(name = "user_type")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCodeDetailEntity userTypeCode;

    @JoinColumn(name = "education")
    @ManyToOne(fetch = FetchType.LAZY)
    private CommonCodeDetailEntity educationCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, name = "font_size")
    private int fontSize;

    @Column(nullable = false, name = "profile_img")
    private String profileImgUrl;

    @Column(columnDefinition = "INT UNSIGNED default 0 NOT NULL")
    private Integer consecutiveDays;
}
