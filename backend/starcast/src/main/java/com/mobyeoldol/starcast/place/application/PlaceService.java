package com.mobyeoldol.starcast.place.application;

import com.mobyeoldol.starcast.place.domain.FavouriteSpot;

public interface PlaceService {
    public FavouriteSpot createFavourite(String placeUid, String profileUid);
    public void deleteFavourite(String spotUid);
}
