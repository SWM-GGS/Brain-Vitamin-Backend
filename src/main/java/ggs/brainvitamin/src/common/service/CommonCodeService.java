package ggs.brainvitamin.src.common.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.entity.CommonCodeEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.common.repository.CommonCodeRepository;
import ggs.brainvitamin.src.post.patient.dto.EmotionInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ggs.brainvitamin.config.BaseResponseStatus.CODE_NOT_EXISTS;

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

    public List<CommonCodeDetailDto> getCodeInfosWithCode(String code) {

        // 공통 코드 테이블을 통해 조회하여 리스트에 저장
        CommonCodeEntity commonCodeEntity = commonCodeRepository.findByCode(code)
                .orElseThrow(() -> new BaseException(CODE_NOT_EXISTS));

        List<CommonCodeDetailDto> codeInfoList = new ArrayList<>();
        for (CommonCodeDetailEntity commonCodeDetailEntity : commonCodeEntity.getCommonCodeDetailEntities()) {
            codeInfoList.add(
                    CommonCodeDetailDto.builder()
                            .id(commonCodeDetailEntity.getId())
                            .codeDetail(commonCodeDetailEntity.getCodeDetail())
                            .codeDetailName(commonCodeDetailEntity.getCodeDetailName())
                            .commonCode(commonCodeEntity)
                            .build()
            );
        }

        return codeInfoList;
    }
}
