package com.mobyeoldol.starcast.member.application.service;

import com.mobyeoldol.starcast.community.domain.Community;
import com.mobyeoldol.starcast.community.domain.repository.CommunityRepository;
import com.mobyeoldol.starcast.member.application.dto.AddressDto;
import com.mobyeoldol.starcast.member.application.dto.AuthorDto;
import com.mobyeoldol.starcast.member.domain.*;
import com.mobyeoldol.starcast.member.domain.repository.*;
import com.mobyeoldol.starcast.member.presentation.request.UpdateMySpotRequest;
import com.mobyeoldol.starcast.member.presentation.response.*;
import com.mobyeoldol.starcast.place.domain.MySpot;
import com.mobyeoldol.starcast.place.domain.Place;
import com.mobyeoldol.starcast.place.domain.repository.MySpotRepository;
import com.mobyeoldol.starcast.place.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final ProfileRepository profileRepository;
    private final RankRepository rankRepository;
    private final CommunityRepository communityRepository;
    private final MySpotRepository mySpotRepository;
    private final PlaceRepository placeRepository;

    @Override
    public void updateMySpot(String profileUid, UpdateMySpotRequest request) {
        log.info("[나의 정보 수정 (내 주소) API] 1. 주소1, 주소2, 주소3, 주소4 정보를 통해 Place 테이블에서 해당 장소를 찾기");
        Optional<Place> optionalPlace = placeRepository.findByAddress1AndAddress2AndAddress3AndAddress4(
                request.getAddress1(), request.getAddress2(), request.getAddress3(), request.getAddress4()
        );

        if (optionalPlace.isEmpty()) {
            log.error("[나의 정보 수정 (내 주소) API] 1-1. 해당하는 장소를 찾을 수 없습니다.");
            throw new IllegalArgumentException("해당하는 장소를 찾을 수 없습니다.");
        }
        Place place = optionalPlace.get();

        log.info("[나의 정보 수정 (내 주소) API] 2. profileUid 확인 후, MySpot 테이블의 place_uid 업데이트");
        Optional<MySpot> optionalMySpot = mySpotRepository.findByProfile_ProfileUid(profileUid);

        if (optionalMySpot.isEmpty()) {
            log.error("[나의 정보 수정 (내 주소) API] 2-2. 해당 프로필에 대한 장소 정보를 찾을 수 없습니다.");
            throw new IllegalArgumentException("해당 프로필에 대한 장소 정보를 찾을 수 없습니다.");
        }
        MySpot mySpot = optionalMySpot.get();

        log.info("[나의 정보 수정 (내 주소) API] 3. MySpot의 장소 아이디를 업데이트");
        mySpot.setPlace(place);
        mySpotRepository.save(mySpot);
    }

    @Override
    public void updateMyNickname(String profileUid, String nickname) {
        log.info("[나의 정보 수정 (닉네임) API] 1. profile 조회");
        Optional<Profile> optionalProfile = profileRepository.findById(profileUid);
        if(optionalProfile.isEmpty()) {
            log.error("[나의 정보 수정 (닉네임) API] 1-2. 해당 프로필 정보를 찾을 수 없습니다.");
            throw new IllegalArgumentException("해당 프로필 정보를 찾을 수 없습니다.");
        }
        Profile profile = optionalProfile.get();

        log.info("[나의 정보 수정 (닉네임) API] 2. 자신이 동일한 닉네임을 다시 설정하려고 하는지 확인");
        if (profile.getNickname().equals(nickname)) {
            log.error("[나의 정보 수정 (닉네임) API] 2-1. 같은 닉네임을 사용 중입니다.");
            throw new IllegalArgumentException("같은 닉네임을 사용 중입니다.");
        }

        log.info("[나의 정보 수정 (닉네임) API] 3. 닉네임 중복 검사");
        Optional<Profile> existingProfile = profileRepository.findByNickname(nickname);
        if (existingProfile.isPresent()) {
            log.error("[나의 정보 수정 (닉네임) API] 2-2. 사용 중인 닉네임입니다.");
            throw new IllegalArgumentException("사용 중인 닉네임입니다.");
        }

        log.info("[나의 정보 수정 (닉네임) API] 3. 닉네임 수정 및 저장");
        profile.setNickname(nickname);
        profileRepository.save(profile);
    }

    @Override
    public void updateMyProfileImage(String profileUid, String image) {
        log.info("[나의 정보 수정 (캐스타이미지) API] 1. profile 조회");
        Optional<Profile> optionalProfile = profileRepository.findById(profileUid);
        if(optionalProfile.isEmpty()) {
            log.error("[나의 정보 수정 (캐스타이미지) API] 1-2. 해당 프로필 정보를 찾을 수 없습니다.");
            throw new IllegalArgumentException("해당 프로필 정보를 찾을 수 없습니다.");
        }
        Profile profile = optionalProfile.get();

        log.info("[나의 정보 수정 (캐스타이미지) API] 2. 캐스타이미지 수정 및 저장");
        profile.setProfileImgNum(image);
        profileRepository.save(profile);
    }


    @Override
    public MyInfoResponse getMemberInfo(String profileUid) {

        log.info("[내 정보 가져오기 API] 1. 유저 정보 인증");
        Optional<Profile> profile = profileRepository.findById(profileUid);
        if (profile.isEmpty()) throw new RuntimeException("존재하지 않는 유저입니다.");

        log.info("[내 정보 가져오기 API] 2. 유저 랭크 확인");
        Optional<Rank> rank = rankRepository.findById(profile.get().getRank().getRankUid());
        if (rank.isEmpty()) throw new RuntimeException("존재하지 않는 랭크입니다.");

        log.info("[내 정보 가져오기 API] 3. 유저의 내 장소 확인");
        Optional<Place> place = mySpotRepository.findByProfileIdAndSpotType(profileUid);
        if (place.isEmpty()) throw new RuntimeException("존재하지 않는 장소입니다.");

        String placeAddress = place.get().getAddress1() + " " + place.get().getAddress2() + " " + place.get().getAddress3();

        log.info("[내 정보 가져오기 API] 4. 내 정보 리턴");
        return MyInfoResponse.builder()
                .name(profile.get().getName())
                .nickname(profile.get().getNickname())
                .email(profile.get().getEmail())
                .profileImage(profile.get().getProfileImgNum())
                .address(placeAddress)
                .myCurExp(profile.get().getExp())
                .rank(rank.get().getName())
                .build();
    }

    public List<CommunityByMemberResponse> getCommunityListByMember(String profileUid) {

        log.info("[내가 작성한 글 리스트 가져오기 API] 1. 내가 작성한 글 리스트 가져오기");
        Optional<List<Community>> communityList = communityRepository.findByProfileIdAndIsDeleted(profileUid);
        if (communityList.isEmpty()) throw new RuntimeException("글 리스트가 존재하지 않습니다.");

        List<CommunityByMemberResponse> communities = new ArrayList<>();

        log.info("[내가 작성한 글 리스트 가져오기 API] 2. 리스트를 응답 형식으로 가공");
        for (Community community : communityList.get()) {
            AuthorDto authorDto = AuthorDto.builder()
                    .profileUid(community.getProfile().getProfileUid())
                    .nickname(community.getProfile().getNickname())
                    .profileImage(community.getProfile().getProfileImgNum())
                    .build();

            AddressDto addressDto = AddressDto.builder()
                    .address1(community.getPlace().getAddress1())
                    .address2(community.getPlace().getAddress2())
                    .address3(community.getPlace().getAddress3())
                    .build();

            CommunityByMemberResponse response = CommunityByMemberResponse.builder()
                    .communityUid(community.getCommunityUid())
                    .mainImage(community.getCommunityImages().getFirst().getUrl())
                    .title(community.getTitle())
                    .author(authorDto)
                    .dateTime(community.getCreatedDate())
                    .address(addressDto)
                    .castarPoint(community.getPlan().getCastarPoint())
                    .build();

            communities.add(response);
        }

        log.info("[내가 작성한 글 리스트 가져오기 API] 3. 글 리스트를 응답 형식으로 가공 후 리턴");
        return communities;
    }
}
