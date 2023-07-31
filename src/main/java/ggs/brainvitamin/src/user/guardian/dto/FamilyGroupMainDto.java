package ggs.brainvitamin.src.user.guardian.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class FamilyGroupMainDto {

    List<FamilyGroupPreviewDto> familyGroupPreviewDtoList = new ArrayList<>();
}
