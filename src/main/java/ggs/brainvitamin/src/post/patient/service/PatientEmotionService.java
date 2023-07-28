package ggs.brainvitamin.src.post.patient.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponseStatus;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.post.entity.EmotionEntity;
import ggs.brainvitamin.src.post.entity.PostEntity;
import ggs.brainvitamin.src.post.patient.dto.EmotionDto;
import ggs.brainvitamin.src.post.patient.dto.PostDetailDto;
import ggs.brainvitamin.src.post.repository.EmotionRepository;
import ggs.brainvitamin.src.post.repository.PostRepository;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.OptionalInt;

import static ggs.brainvitamin.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientEmotionService {

    private final CommonCodeDetailRepository commonCodeDetailRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EmotionRepository emotionRepository;

    public void addEmotion(Long postId, Long userId, Long emotionId) throws BaseException {

        // 게시글이 존재하지 않는 경우
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(POST_NOT_EXISTS));

        // 유저를 찾을 수 없는 경우
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));

        // 감정표현이 존재하지 않는 경우
        CommonCodeDetailEntity commonCodeDetailEntity = commonCodeDetailRepository.findById(emotionId)
                .orElseThrow(() -> new BaseException(EMOTION_NOT_EXISTS));

        // 게시글의 감정표현 갯수 증가 후 저장
        postEntity.increaseEmotionsCount();
        postRepository.save(postEntity);

        // 감정표현 정보 저장
        emotionRepository.save(
                EmotionEntity.builder()
                        .post(postEntity)
                        .user(userEntity)
                        .emotionTypeCode(commonCodeDetailEntity)
                        .build()
        );
    }
}
