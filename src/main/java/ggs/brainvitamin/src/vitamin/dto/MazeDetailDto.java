package ggs.brainvitamin.src.vitamin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MazeDetailDto {
    private Double x;
    private Double y;
    private Boolean answer;

    public MazeDetailDto(Double x, Double y) {
        this.x = x;
        this.y = y;
        this.answer = false;
    }
}
