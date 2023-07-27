package ggs.brainvitamin.src.vitamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetPatientHomeDto {
    private Boolean first;

    private String nextToDo;

    private Integer consecutiveDays;
}
