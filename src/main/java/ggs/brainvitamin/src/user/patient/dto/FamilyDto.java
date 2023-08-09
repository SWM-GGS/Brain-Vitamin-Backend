package ggs.brainvitamin.src.user.patient.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FamilyDto {

    private Long id;
    private String familyName;
    private String familyKey;
    private Integer familyExp;
    private Integer familyLevel;
    private Integer memberCount;
}
