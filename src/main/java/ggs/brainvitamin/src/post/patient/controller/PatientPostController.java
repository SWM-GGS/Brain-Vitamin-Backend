package ggs.brainvitamin.src.post.patient.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.post.patient.dto.PostDto;
import ggs.brainvitamin.src.post.patient.dto.PostMainDto;
import ggs.brainvitamin.src.post.patient.service.PatientPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patient/family-stories")
@RequiredArgsConstructor
public class PatientPostController {

    private final PatientPostService patientPostService;

    @GetMapping("/{familyId}")
    public BaseResponse<List<PostMainDto>> getFamilyStoriesMain(@PathVariable("familyId") Long familyId) {
        try {
            return new BaseResponse<>(patientPostService.listFamilyStoriesMain(familyId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

//    @GetMapping("/{familyId}/{postId}")
//    public BaseResponse<PostDto> getFamilyStoriesPost(@PathVariable("postId") Long postId) {
//        try {
//            return BaseResponse<>(patientPostService.findFamilyStoriesPost(postId));
//        } catch (BaseException e) {
//            return new BaseResponse<>(e.getStatus());
//        }
//    }
}

