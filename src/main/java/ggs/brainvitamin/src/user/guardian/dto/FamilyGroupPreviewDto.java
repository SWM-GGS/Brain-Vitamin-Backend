package ggs.brainvitamin.src.user.guardian.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FamilyGroupPreviewDto {

    private Long id;
    private String profileImgUrl;
    private String familyName;
}
