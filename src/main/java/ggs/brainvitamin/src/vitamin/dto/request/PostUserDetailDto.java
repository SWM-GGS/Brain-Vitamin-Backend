package ggs.brainvitamin.src.vitamin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUserDetailDto {

    private String birthDate;
    private String gender;
    private String education;

}
