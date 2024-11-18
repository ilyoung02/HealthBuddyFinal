package com.example.healthbuddypro.Gym;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.healthbuddypro.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GymFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private List<Marker> currentMarkers = new ArrayList<>(); // 현재 표시된 마커들을 관리하는 리스트

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gym, container, false);

        // Places API 초기화
        Places.initialize(requireContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(requireContext());

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Google Maps 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 검색 입력 필드
        EditText searchGymEditText = view.findViewById(R.id.editTextSearchGym);
        searchGymEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 아무 작업도 하지 않음
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = searchGymEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchGymByName(query); // 입력된 검색어로 헬스장 검색
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 아무 작업도 하지 않음
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 위치 권한 확인 및 요청
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            requestLocationPermission();
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);  // 내 위치 활성화

            // FusedLocationProviderClient로 내 위치 얻기
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(android.location.Location location) {
                            if (location != null) {
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));  // 내 위치로 카메라 이동
                            }
                        }
                    });
        }
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                showErrorMessage("위치 권한이 거부되었습니다.");
            }
        }
    }

    // 헬스장 검색 기능: 사용자가 입력한 검색어로 헬스장 이름 검색
    private void searchGymByName(String query) {
        try {
            // 대구 좌표 범위 설정
            LatLng southWest = new LatLng(35.8353, 128.500);
            LatLng northEast = new LatLng(35.9123, 129.2000);

            // 장소 필드 설정
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            // 텍스트 검색 요청 생성
            SearchByTextRequest searchByTextRequest = SearchByTextRequest.builder(query, placeFields)
                    .setMaxResultCount(10)
                    .setLocationRestriction(RectangularBounds.newInstance(southWest, northEast))
                    .build();

            // PlacesClient를 사용하여 검색 수행
            placesClient.searchByText(searchByTextRequest)
                    .addOnSuccessListener(response -> {
                        // 기존에 있는 마커들을 지움
                        for (Marker marker : currentMarkers) {
                            marker.remove();
                        }
                        currentMarkers.clear(); // 리스트 초기화

                        List<Place> places = response.getPlaces();
                        if (places.size() > 0) {
                            for (Place place : places) {
                                LatLng latLng = place.getLatLng();
                                if (latLng != null) {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(place.getName()));
                                    currentMarkers.add(marker); // 새로 추가된 마커를 리스트에 추가
                                }
                            }

                            // 첫 번째 마커를 중심으로 카메라 이동하고 줌 레벨 30으로 설정
                            LatLng firstPlaceLatLng = places.get(0).getLatLng();
                            if (firstPlaceLatLng != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPlaceLatLng, 17));  // 줌 레벨 30으로 설정
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("GymFragment", "검색 실패: " + e.getMessage());
                        showErrorMessage("헬스장 정보를 불러오는 중 오류가 발생했습니다.");
                    });
        } catch (Exception e) {
            Log.e("GymFragment", "검색 중 오류 발생: " + e.getMessage());
        }
    }


    private void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
