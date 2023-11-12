package ggs.brainvitamin.src.user.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FamilyPictureProblemDto {
    private String problemType;

    private String imgUrl;

    private String script;

    private List<Object> choices;

    private Integer answerIndex;

}
