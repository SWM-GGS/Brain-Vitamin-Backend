package ggs.brainvitamin.config;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "commonCodeDetail")
@Getter
public class CommonCodeDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(nullable = false)
    private String codeDetail;

    @Column(nullable = false)
    private String codeDetailName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_id")
    private CommonCodeEntity commonCode;
}
