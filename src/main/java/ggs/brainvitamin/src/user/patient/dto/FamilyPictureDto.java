package ggs.brainvitamin.src.user.patient.dto;

import ggs.brainvitamin.config.Season;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FamilyPictureDto {
    @NotBlank(message = "잘못된 URL 링크입니다.")
    @Pattern(regexp = "((http[s]?|ftp):\\/\\/)?(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=가-힣]{1,256}[:|\\.][a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+,.~#?&\\/=가-힣]*)", message = "잘못된 URL 링크입니다.")
    private String imgUrl;

    @NotNull(message = "season 값을 입력해주세요")
    private Season season;

    @NotBlank(message = "성별을 입력해주세요")
    @Size(max = 300, message = "최대 300자까지 입력 가능합니다")
    private String place;

    @NotNull(message = "사진에 나오는 인원수를 입력해주세요")
    @Min(value = 0, message = "잘못된 인원수입니다")
    @Max(value = 100, message = "잘못된 인원수입니다")
    private Integer headCount;

    @NotNull(message = "사진 찍은 년도를 입력해주세요")
    @Min(value = 1800, message = "잘못된 년도입니다")
    @Max(value = 3000, message = "잘못된 년도입니다")
    private Integer year;

    @Size(max = 30, message = "잘못된 가족 관계입니다")
    private List<Integer> FamilyRelations;

}
