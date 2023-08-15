package ggs.brainvitamin.src.user.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class FamilyMemberDto {

    private String name;
    private String relationship;
    private String profileImg;
}
