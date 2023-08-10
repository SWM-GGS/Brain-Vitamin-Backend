package ggs.brainvitamin.src.common.service;

import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.common.repository.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final CommonCodeRepository commonCodeRepository;
    private final CommonCodeDetailRepository commonCodeDetailRepository;

    public CommonCodeDetailDto getCodeWithCodeDetailName(String codeDetailName) {

        CommonCodeDetailEntity codeDetailEntity =
                commonCodeDetailRepository.findCommonCodeDetailEntityByCodeDetailName(codeDetailName);

        CommonCodeDetailDto codeDetailDto = CommonCodeDetailDto.builder()
                .id(codeDetailEntity.getId())
                .codeDetail(codeDetailEntity.getCodeDetail())
                .codeDetailName(codeDetailEntity.getCodeDetailName())
                .commonCode(codeDetailEntity.getCommonCode())
                .build();

        return codeDetailDto;
    }
}
