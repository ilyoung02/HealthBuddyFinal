package com.example.healthbuddypro.Gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthbuddypro.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GymFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gym, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 마커 추가
        // 샘플 헬스장 위치 추가 (나중에 실제 데이터를 이용하여 위치 추가)
        LatLng gymLocation1 = new LatLng(51.5074, -0.1278); // Example location
        mMap.addMarker(new MarkerOptions().position(gymLocation1).title("헬스장 1").snippet("헬스장 세부 정보"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gymLocation1, 12));

        LatLng gymLocation2 = new LatLng(51.5154, -0.0728); // Example location
        mMap.addMarker(new MarkerOptions().position(gymLocation2).title("헬스장 2").snippet("헬스장 세부 정보"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gymLocation2, 12));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 여기서 팝업 또는 추가 세부정보를 표시할 수 있습니다.
                return false; // false를 반환하면 기본 동작으로 인해 정보 창이 표시됩니다.
            }
        });
    }
}
