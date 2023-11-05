package ggs.brainvitamin.src.post.patient.dto;

import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.user.patient.dto.FamilyMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PostMainDto {

    private List<PostPreviewDto> postPreviewDtoList;
    private List<CommonCodeDetailDto> emotionInfoDtoList;
    private List<FamilyMemberDto> familyMemberDtoList;
}
