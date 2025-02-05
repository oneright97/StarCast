package com.mobyeoldol.starcast.place.presentation;

import com.mobyeoldol.starcast.auth.application.service.AuthService;
import com.mobyeoldol.starcast.global.template.BaseResponseTemplate;
import com.mobyeoldol.starcast.place.application.PlaceService;
import com.mobyeoldol.starcast.place.domain.FavouriteSpot;
import com.mobyeoldol.starcast.place.domain.enums.MainPlace;
import com.mobyeoldol.starcast.place.presentation.request.CreatePlanRequest;
import com.mobyeoldol.starcast.place.presentation.request.GetPlaceListRequest;
import com.mobyeoldol.starcast.place.presentation.request.ModifyPlanRequest;
import com.mobyeoldol.starcast.place.presentation.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/place")
public class PlaceController {

    private final PlaceService placeService;
    private final AuthService authService;

    @PostMapping("/{place_uid}/favourite")
    public ResponseEntity<BaseResponseTemplate<?>> createFavouriteSpot(
            @PathVariable(value = "place_uid") String placeUid,
            @RequestHeader(value = "Authorization") String bearerToken
    ){
        log.info("[즐겨찾기 등록 API] POST /api/v1/place/{}/favourite", placeUid);
        String profileUid = authService.authenticateMember(bearerToken);

        try {
            log.info("[즐겨찾기 등록 API] 즐겨찾기 등록 Service 로직 수행");
            FavouriteSpot favouriteSpot = placeService.createFavouriteSpot(placeUid, profileUid);

            log.info("[즐겨찾기 등록 API] 정상 처리되어 201 반환");
            FavouriteSpotUidResponse responseDto = new FavouriteSpotUidResponse(favouriteSpot.getSpotUid());

            BaseResponseTemplate<FavouriteSpotUidResponse> result = BaseResponseTemplate.success(responseDto);
            return ResponseEntity.status(201).body(result);
        } catch (IllegalStateException e) {
            log.error("[즐겨찾기 등록 API] 즐겨찾기가 이미 등록되어 409 반환");
            BaseResponseTemplate<?> errorResponse = BaseResponseTemplate.failure(409, "이미 즐겨찾기가 등록되었습니다.");
            return ResponseEntity.status(409).body(errorResponse);
        }
    }

    @GetMapping("/favourite/{favourite_spot_uid}")
    public ResponseEntity<BaseResponseTemplate<?>> getFavouriteSpot(
            @PathVariable("favourite_spot_uid") String favouriteSpotUid,
            @RequestHeader("Authorization") String bearerToken
    ) {
        log.info("[즐겨찾기 하나 조회 API] GET /api/v1/place/favourite/{}",favouriteSpotUid);
        String profileUid = authService.authenticateMember(bearerToken);

        FavouriteSpotResponse favouriteSpot = placeService.getFavouriteSpot(favouriteSpotUid, profileUid);
        return ResponseEntity.ok(BaseResponseTemplate.success(favouriteSpot));
    }

    @GetMapping("/favourite")
    public ResponseEntity<BaseResponseTemplate<List<?>>> getFavouriteSpots(
            @RequestHeader("Authorization") String bearerToken
    ) {
        log.info("[즐겨찾기 모두 조회 API] GET /api/v1/place/favourite");
        String profileUid = authService.authenticateMember(bearerToken);

        List<FavouriteSpotResponse> favouriteSpots = placeService.getFavouriteSpots(profileUid);
        return ResponseEntity.ok(BaseResponseTemplate.success(favouriteSpots));
    }

    @DeleteMapping("/favourite/{favourite_spot_uid}")
    public ResponseEntity<BaseResponseTemplate<?>> deleteFavouriteSpot(
            @PathVariable(value = "favourite_spot_uid") String favouriteSpotUid,
            @RequestHeader(value = "Authorization") String bearerToken
    ) {
        log.info("[즐겨찾기 삭제 API] DELETE /api/v1/place/favourite/{}", favouriteSpotUid);
        String profileUid = authService.authenticateMember(bearerToken);

        try {
            log.info("[즐겨찾기 삭제 API] 즐겨찾기 삭제 Service 로직 수행");
            placeService.deleteFavouriteSpot(favouriteSpotUid);

            BaseResponseTemplate<?> successResponse = BaseResponseTemplate.success(null);
            return ResponseEntity.ok().body(successResponse);
        } catch (IllegalArgumentException e) {
            log.error("[즐겨찾기 삭제 API] 즐겨찾기 항목을 찾을 수 없는 경우 404 반환");
            BaseResponseTemplate<?> errorResponse = BaseResponseTemplate.failure(404, "해당 즐겨찾기를 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping("/{place_uid}")
    public ResponseEntity<BaseResponseTemplate<?>> getPlaceDetails(
            @PathVariable(value = "place_uid") String placeUid,
            @RequestHeader(value = "Authorization") String bearerToken
    ) {
        log.info("[장소 하나 자세히 보기 API] GET /api/v1/place/{}", placeUid);
        String profileUid = authService.authenticateMember(bearerToken);

        try {
            log.info("[장소 하나 자세히 보기 API] 장소 조회 Service 로직 수행");
            PlaceDetailsResponse placeDetailsResponse = placeService.getPlaceDetails(placeUid);

            BaseResponseTemplate<PlaceDetailsResponse> successResponse = BaseResponseTemplate.success(placeDetailsResponse);
            return ResponseEntity.ok().body(successResponse);
        } catch (IllegalArgumentException e) {
            log.error("[장소 하나 자세히 보기 API] 장소를 찾을 수 없는 경우 404 반환");
            BaseResponseTemplate<?> errorResponse = BaseResponseTemplate.failure(404, "해당 장소를 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping()
    public ResponseEntity<BaseResponseTemplate<?>> getPlaceList(
            @Valid @RequestBody GetPlaceListRequest request,
            BindingResult bindingResult,
            @RequestHeader(value = "Authorization") String bearerToken
    ){
        log.info("[관측지 리스트 보기 API] GET /api/v1/place");
        if (bindingResult.hasErrors()) {
            log.error("[관측지 리스트 보기 API] 유효성 검사 실패: {}", bindingResult.getFieldError().getDefaultMessage());
            BaseResponseTemplate<?> errorResponse = BaseResponseTemplate.failure(400, "[관측지 리스트 보기 API] placeType과 sortBy는 필수입니다.");
            return ResponseEntity.status(400).body(errorResponse);
        }
        String profileUid = authService.authenticateMember(bearerToken);

        log.info("[관측지 리스트 보기 API] Service 로직 수행");
        GetPlaceListResponse response = placeService.getPlaceList(request);

        BaseResponseTemplate<?> successResponse = BaseResponseTemplate.success(response);
        return ResponseEntity.ok().body(successResponse);
    }

    @PatchMapping("/update-action/{place_type}")
    public ResponseEntity<BaseResponseTemplate<?>> updateFavourite(
            @PathVariable(value = "place_type") MainPlace mainPlace,
            @RequestHeader(value = "Authorization") String bearerToken
    ) {
        log.info("[메인 장소 유형 업데이트 API] PATCH /api/v1/place/update-action/{}", mainPlace);
        String profileUid = authService.authenticateMember(bearerToken);

        log.info("[메인 장소 유형 업데이트 API] Service 로직 수행");
        placeService.updateActionPlaceType(profileUid, mainPlace);

        BaseResponseTemplate<?> successResponse = BaseResponseTemplate.success("메인 장소 유형이 성공적으로 업데이트되었습니다.");
        return ResponseEntity.ok().body(successResponse);
    }


    @PostMapping("/plan/make-plan")
    public ResponseEntity<BaseResponseTemplate<?>> makePlan(
            @Valid @RequestBody CreatePlanRequest request,
            BindingResult bindingResult,
            @RequestHeader(value = "Authorization") String bearerToken
    ) {
        log.info("[장소 찜 생성 API] POST /api/v1/place/plan/makePlan");

        if (bindingResult.hasErrors()) {
            log.error("[장소 찜 생성 API] 유효성 검사 실패: {}", bindingResult.getFieldError().getDefaultMessage());
            BaseResponseTemplate<?> errorResponse = BaseResponseTemplate.failure(400, "[장소 찜 생성 API] 장소 아이디와 날짜는 필수입니다. [날짜형식 : yyyy-MM-dd'T'HH:mm:ss]");
            return ResponseEntity.status(400).body(errorResponse);
        }

        String profileUid = authService.authenticateMember(bearerToken);

        log.info("[장소 찜 생성 API] Service 로직 수행");
        PlanUidResponse response = placeService.makePlan(request, profileUid);

        BaseResponseTemplate<PlanUidResponse> successResponse = BaseResponseTemplate.success(response);
        return ResponseEntity.status(201).body(successResponse);
    }

    @GetMapping("/plan")
    public ResponseEntity<BaseResponseTemplate<?>> getPlanDetails(
            @RequestHeader(value = "Authorization") String bearerToken
    ) {
        log.info("[장소 찜 이력 모두 조회 API] GET /api/v1/place/plan");
        String profileUid = authService.authenticateMember(bearerToken);

        log.info("[장소 찜 이력 모두 조회 API] Service 로직 수행");
        PlanListResponse response = placeService.getPlanList(profileUid);

        BaseResponseTemplate<PlanListResponse> successResponse = BaseResponseTemplate.success(response);
        return ResponseEntity.ok().body(successResponse);
    }

    @GetMapping("/plan/{plan_uid}")
    public ResponseEntity<BaseResponseTemplate<?>> getPlanDetails(
            @PathVariable(value = "plan_uid") String planUid,
            @RequestHeader(value = "Authorization") String bearerToken
    ) {
        log.info("[장소 찜 하나 조회 API] GET /api/v1/place/plan/{}", planUid);
        String profileUid = authService.authenticateMember(bearerToken);

        log.info("[장소 찜 하나 조회 API] Service 로직 수행");
        PlanDetailsResponse response = placeService.getPlanDetails(planUid, profileUid);

        BaseResponseTemplate<PlanDetailsResponse> successResponse = BaseResponseTemplate.success(response);
        return ResponseEntity.ok().body(successResponse);
    }

    @PatchMapping("plan/change-plan")
    public ResponseEntity<BaseResponseTemplate<?>> changePlan(
            @Valid @RequestBody ModifyPlanRequest request,
            BindingResult bindingResult,
            @RequestHeader(value = "Authorization") String bearerToken
    ) {
        log.info("[장소 찜 수정 API] PATCH /api/v1/place/plan/changePlan");

        if (bindingResult.hasErrors()) {
            log.error("[장소 찜 수정 API] 유효성 검사 실패: {}", bindingResult.getFieldError().getDefaultMessage());
            BaseResponseTemplate<?> errorResponse = BaseResponseTemplate.failure(400, "[장소 찜 수정 API] 찜 아이디는 필수입니다.");
            return ResponseEntity.status(400).body(errorResponse);
        }

        String profileUid = authService.authenticateMember(bearerToken);

        if (request.getDateTime() == null && request.getPlaceUid() == null) {
            BaseResponseTemplate<?> errorResponse = BaseResponseTemplate.failure(400, "placeUid 또는 dateTime 중 하나는 반드시 포함되어야 합니다.");
            return ResponseEntity.status(400).body(errorResponse);
        }

        log.info("[장소 찜 수정 API] Service 로직 수행");
        PlanDetailsResponse response = placeService.changePlan(request, profileUid);

        BaseResponseTemplate<PlanDetailsResponse> successResponse = BaseResponseTemplate.success(response);
        return ResponseEntity.ok().body(successResponse);
    }

    @DeleteMapping("/plan/{plan_uid}")
    public ResponseEntity<BaseResponseTemplate<?>> deletePlan(
            @PathVariable(value = "plan_uid") String planUid,
            @RequestHeader(value = "Authorization") String bearerToken
    ) {
        log.info("[장소 찜 삭제 API] DELETE /api/v1/place/plan/{}", planUid);
        String profileUid = authService.authenticateMember(bearerToken);

        log.info("[장소 찜 삭제 API] Service 로직 수행");
        placeService.deletePlan(planUid, profileUid);

        BaseResponseTemplate<?> successResponse = BaseResponseTemplate.success(null);
        return ResponseEntity.ok().body(successResponse);
    }
}
