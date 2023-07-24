package ggs.brainvitamin.src.vitamin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MazeDto {
    private String imgUrl;
    private List<MazeDetailDto> mazeDetailDtoList = new ArrayList<MazeDetailDto>();

    public MazeDto(String imgUrl) {
        this.imgUrl = imgUrl;
        MazeDetailDto mazeDetailDto1 = new MazeDetailDto(1.0, 1.0);
        MazeDetailDto mazeDetailDto2 = new MazeDetailDto(2.0, 2.0);
        MazeDetailDto mazeDetailDto3 = new MazeDetailDto(3.0, 3.0);
        mazeDetailDto1.setAnswer(true);

        this.mazeDetailDtoList.add(mazeDetailDto1);
        this.mazeDetailDtoList.add(mazeDetailDto2);
        this.mazeDetailDtoList.add(mazeDetailDto3);
    }
}
