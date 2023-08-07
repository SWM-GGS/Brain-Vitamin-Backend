package ggs.brainvitamin.src.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@Table(name = "REFRESH_TOKEN")
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private String deviceIdentifier;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void updateDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }
}
