package ggs.brainvitamin.src.post.patient.service;

import ggs.brainvitamin.src.post.entity.PostEntity;
import ggs.brainvitamin.src.post.entity.PostImgEntity;
import ggs.brainvitamin.src.post.patient.dto.PostDto;
import ggs.brainvitamin.src.post.patient.dto.PostImgDto;
import ggs.brainvitamin.src.post.patient.dto.PostMainDto;
import ggs.brainvitamin.src.post.repository.PostRepository;
import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import ggs.brainvitamin.src.user.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientPostService {

    private final PostRepository postRepository;
    private final FamilyMemberRepository familyMemberRepository;

    public List<PostMainDto> listFamilyStoriesMain(Long familyId) {

        // familyId로 해당 가족 그룹의 게시글을 조회
        List<PostEntity> familyPostResults = postRepository.findByFamilyId(familyId);
        List<PostMainDto> familyPostMainList = new ArrayList<>();

        // 각 postEntity에서 게시글 id와 첫번째 이미지를 가져와 postMainDto에 저장
        for (PostEntity post : familyPostResults) {
            familyPostMainList.add(
                    PostMainDto.builder()
                            .id(post.getId())
                            .thumbnailUrl(post.getPostImgEntityList().get(0).getImgUrl())
                            .build()
            );
        }

        return familyPostMainList;
    }

    public PostDto findFamilyStoriesPost(Long postId) {

        //post 테이블에서 해당 게시글 조회
        Optional<PostEntity> postResult = postRepository.findById(postId);
        postResult.ifPresent(p -> {
            PostEntity resultValues = postResult.get();

            // 함께 조회된 게시글의 이미지 리스트에 대하여 Entity -> Dto
            List<PostImgDto> postImgDtoList = new ArrayList<>();
            for (PostImgEntity imgEntity : resultValues.getPostImgEntityList()) {
                postImgDtoList.add(
                        PostImgDto.builder()
                                .id(imgEntity.getId())
                                .imgUrl(imgEntity.getImgUrl())
                                .description(imgEntity.getDescription())
                                .build()
                );
            }

            // 함께 조회된 게시글의 댓글 리스트에 대히여 Entity -> Dto

            // familyMember 테이블에서 작성자의 가족 관계 조회
            Optional<FamilyMemberEntity> familyMemberInfo = familyMemberRepository.findById(resultValues.getUser().getId());
            String relationship = familyMemberInfo.get().getRelationship();

            PostDto buildResult = PostDto.builder()
                    .id(resultValues.getId())
                    .contents(resultValues.getContents())
                    .postImgDtoList(postImgDtoList)
                    .createdAt(resultValues.getCreatedAt().toString())
                    .userId(resultValues.getUser().getId())
                    .userName(resultValues.getUser().getName())
                    .relationship(relationship)
                    .profileImgUrl(resultValues.getUser().getProfileImgUrl())
                    .viewersCount(resultValues.getViewersCount())
                    .emotionsCount(resultValues.getEmotionsCount())
                    .commentsCount(resultValues.getCommentsCount())
                    .build();

        });

        return null;
    }
}
