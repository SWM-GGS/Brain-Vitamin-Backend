package ggs.brainvitamin.src.user.guardian.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FamilyGroupDetailDto {

    private Long id;
    private String familyName;
    private String profileImgUrl;
    private Integer memberCount;
    private String firstUserName;
}
