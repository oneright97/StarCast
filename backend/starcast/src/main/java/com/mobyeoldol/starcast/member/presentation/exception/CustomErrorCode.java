package com.mobyeoldol.starcast.member.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode implements ErrorCode{

    PROFILE_INFO_NOT_FOUND(HttpStatus.BAD_REQUEST, "프로필 정보가 존재하지 않습니다."),
    RANK_INFO_NOT_FOUND(HttpStatus.BAD_REQUEST, "등급 정보가 존재하지 않습니다."),
    MY_PLACE_INFO_NOT_FOUND(HttpStatus.BAD_REQUEST, "'내 장소' 정보가 존재하지 않습니다."),
    COMMUNITY_LIST_NOT_FOUND(HttpStatus.BAD_REQUEST, "커뮤니티 리스트가 존재하지 않습니다.")
    ;

    public final HttpStatus httpStatus;
    public final String message;
}
