package ggs.brainvitamin.src.vitamin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MarketDto {
    private String category;
    private String elementName;
    private Integer price;
    private Integer count;
    private String imgUrl;
}
