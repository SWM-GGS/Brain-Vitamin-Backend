package ggs.brainvitamin.src.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "commonCodeDetail")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
