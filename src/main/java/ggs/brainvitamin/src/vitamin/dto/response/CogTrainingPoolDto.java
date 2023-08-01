package ggs.brainvitamin.src.vitamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CogTrainingPoolDto {

    private String imgUrl;

    private String contents;

    private Boolean answer;

    private Integer price;

    private Integer count;

    private Float x;

    private Float y;

    public CogTrainingPoolDto(String imgUrl, String contents, Integer price, Integer count) {
        this.imgUrl = imgUrl;
        this.contents = contents;
        this.price = price;
        this.count = count;
    }
}
