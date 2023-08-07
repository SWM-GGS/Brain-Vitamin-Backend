package ggs.brainvitamin.src.common.dto;

import ggs.brainvitamin.src.common.entity.CommonCodeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonCodeDetailDto {

    private Long id;
    private String codeDetail;
    private String codeDetailName;
    private CommonCodeEntity commonCode;
}
