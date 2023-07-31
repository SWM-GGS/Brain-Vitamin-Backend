package ggs.brainvitamin.src.user.guardian.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FamilyGroupJoinDto {

    private Long familyId;
    private String relationship;
}
