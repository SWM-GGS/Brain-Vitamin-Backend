package ggs.brainvitamin.src.user.patient.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponseStatus;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.FamilyEntity;
import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.patient.dto.FamilyDto;
import ggs.brainvitamin.src.user.repository.FamilyMemberRepository;
import ggs.brainvitamin.src.user.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static ggs.brainvitamin.src.user.patient.dto.PatientUserDto.*;

@Service
@RequiredArgsConstructor
public class PatientFamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;

    public void createFamily(PatientDetailDto patientDetailDto) {

        // 중복되지 않는 key 값이 생성될 때까지 반복
        String familyKey;
        while (true) {
            familyKey = generateFamilyKey();
            if (!familyRepository.findByFamilyKey(familyKey).isPresent()) break;
        }

        FamilyEntity newFamily = FamilyEntity.builder()
                .familyKey(generateFamilyKey())
                .familyLevel(0)
                .familyExp(0)
                .memberCount(1)
                .familyName(patientDetailDto.getNickname() + "님네 가족")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(patientDetailDto.getId())
                .build();

        FamilyMemberEntity newFamilyMember = FamilyMemberEntity.builder()
                .family(newFamily)
                .user(userEntity)
                .relationship("환자 본인")
                .build();

        familyRepository.save(newFamily);
        familyMemberRepository.save(newFamilyMember);
    }

    public FamilyDto getFamilyInfo(Long userId) {

        List<FamilyMemberEntity> familyMember = familyMemberRepository.findByUserIdAndStatus(userId, Status.ACTIVE);

        if (familyMember.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAMILY_NOT_EXISTS);
        }

        FamilyEntity family = familyMember.get(0).getFamily();

        return FamilyDto.builder()
                .id(family.getId())
                .familyName(family.getFamilyName())
                .familyLevel(family.getFamilyLevel())
                .familyExp(family.getFamilyExp())
                .familyKey(family.getFamilyKey())
                .build();
    }

    private String generateFamilyKey() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder familyKey = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            familyKey.append(randomChar);
        }

        return familyKey.toString();
    }
}
