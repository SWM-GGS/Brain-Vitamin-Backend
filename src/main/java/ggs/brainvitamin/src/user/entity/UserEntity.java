package ggs.brainvitamin.src.user.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Table(name = "USER")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(columnDefinition = "INT UNSIGNED default 0 NOT NULL")
    private Integer todayVitaminCheck;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<AuthorityEntity> authorities;

    /**
     * 비즈니스 코드
     */
    public void addPatientDetails(LocalDate birthDate, String gender, CommonCodeDetailEntity educationCode) {
        this.birthDate = birthDate;
        this.gender = gender;
        this.educationCode = educationCode;
    }

    public void plusConsecutiveDays() {
        this.consecutiveDays++;
    }

    public void setConsecutiveDays(Integer consecutiveDays) {
        this.consecutiveDays = consecutiveDays;
    }

    public void setTodayVitaminCheck(Integer todayVitaminCheck) {
        this.todayVitaminCheck = todayVitaminCheck;
    }

    public void updateProfiles(String nickname, String profileImgUrl, CommonCodeDetailEntity educationCode) {
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.educationCode = educationCode;
    }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setFontSize(Integer fontSize) { this.fontSize = fontSize; }
}
