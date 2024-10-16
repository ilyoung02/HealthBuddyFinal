package com.example.healthbuddypro.Profile;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthbuddypro.ApiService;
import com.example.healthbuddypro.Profile.EditUserProfile.GymLocation;
import com.example.healthbuddypro.R;
import com.example.healthbuddypro.RetrofitClient;
import com.google.gson.Gson;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SET_LOCATION = 1;
    private static final String LOCATION_PREFS = "LocationData";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private final String BASE_URL = "http://165.229.89.154:8080/";

    private Uri uri;
    private ImageView imageView;
    private TextView gymLocation, changePhotoButton;;
    private Spinner workoutGoalSpinner, regionSpinner;
    private EditText workoutExperienceInput, introductionInput, favWorkoutsInput;
    private RadioGroup profileVisibleRadioGroup;
    private Button confirmButton, cancelButton, gymLocationBtn;

    // 갤러리에서 이미지 선택 결과를 처리할 ActivityResultLauncher
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    uri = result.getData().getData();  // 선택한 이미지의 URI 가져오기
                    if (uri != null) {
                        imageView.setImageURI(uri);    // ImageView에 선택한 이미지 표시
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initializeViews();
        loadGymLocation();
        requestPermissions();

        gymLocationBtn.setOnClickListener(v -> openLocationPicker());
        cancelButton.setOnClickListener(v -> finish());
        confirmButton.setOnClickListener(v -> updateProfile());

        // 갤러리에서 이미지 선택하는 버튼 리스너 추가
        changePhotoButton.setOnClickListener(v -> openGallery());
    }

    // 화면에 나타나는 것들 선택
    private void initializeViews() {
        imageView = findViewById(R.id.profile_image);
        workoutGoalSpinner = findViewById(R.id.workout_goal_spinner);
        regionSpinner = findViewById(R.id.region_spinner);
        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);
        gymLocationBtn = findViewById(R.id.gym_location_button);
        gymLocation = findViewById(R.id.gym_location);
        workoutExperienceInput = findViewById(R.id.workout_experience_input);
        introductionInput = findViewById(R.id.introduction_input);
        favWorkoutsInput = findViewById(R.id.favWorkouts_input);
        profileVisibleRadioGroup = findViewById(R.id.radioGroupProfileVisible);
        changePhotoButton = findViewById(R.id.change_photo);  // 사진 변경 버튼

        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.workout_goals_array, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutGoalSpinner.setAdapter(goalAdapter);

        ArrayAdapter<CharSequence> regionAdapter = ArrayAdapter.createFromResource(this,
                R.array.regions_array, android.R.layout.simple_spinner_item);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(regionAdapter);
    }

    private void openLocationPicker() {
        Intent setLocationIntent = new Intent(getApplicationContext(), set_location.class);
        startActivityForResult(setLocationIntent, REQUEST_CODE_SET_LOCATION);
    }

    private void loadGymLocation() {
        SharedPreferences sharedPreferences = getSharedPreferences(LOCATION_PREFS, Context.MODE_PRIVATE);
        float latitude = sharedPreferences.getFloat(KEY_LATITUDE, 0);
        float longitude = sharedPreferences.getFloat(KEY_LONGITUDE, 0);

        if (latitude != 0 && longitude != 0) {
            gymLocation.setText(String.format("위도: %s, 경도: %s", latitude, longitude));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_LOCATION && resultCode == RESULT_OK) {
            if (data != null) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                saveGymLocation(latitude, longitude);
                gymLocation.setText(String.format("위도: %s, 경도: %s", latitude, longitude));
                gymLocationBtn.setText("등록 완료");
                showToast("위치가 설정되었습니다: \n" + gymLocation.getText());
            }
        }
    }

    private void saveGymLocation(double latitude, double longitude) {
        SharedPreferences sharedPreferences = getSharedPreferences(LOCATION_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_LATITUDE, (float) latitude);
        editor.putFloat(KEY_LONGITUDE, (float) longitude);
        editor.apply();
    }

    // 갤러리를 여는 메서드
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);  // 갤러리 실행
    }

    private void updateProfile() {
        String selectedGoal = workoutGoalSpinner.getSelectedItem().toString();
        String workoutExperience = workoutExperienceInput.getText().toString();
        String introduction = introductionInput.getText().toString();

        // favWorkoutsInput에서 입력받은 문자열을 쉼표로 나눠서 List로 변환
        String favWorkoutsInputString = favWorkoutsInput.getText().toString();
        List<String> favWorkouts = Arrays.asList(favWorkoutsInputString.split("\\s*,\\s*"));

        int selectedRadioButtonId = profileVisibleRadioGroup.getCheckedRadioButtonId();

        String selectedRegion = regionSpinner.getSelectedItem().toString();
        boolean isProfileVisible = profileVisibleRadioGroup.getCheckedRadioButtonId() == R.id.radioYes;

        SharedPreferences sharedPreferences = getSharedPreferences(LOCATION_PREFS, Context.MODE_PRIVATE);
        float latitude = sharedPreferences.getFloat(KEY_LATITUDE, 0);
        float longitude = sharedPreferences.getFloat(KEY_LONGITUDE, 0);

        // 필수 값 검증
        if (selectedGoal.isEmpty() || workoutExperience.isEmpty() || introduction.isEmpty() ||
                favWorkoutsInputString.isEmpty() || latitude == 0 || longitude == 0 || selectedRegion.isEmpty()) {
            showToast("모든 필수 정보를 입력하세요.");
            return;  // 필수 값이 없으면 API 호출하지 않음
        }
        if (selectedRadioButtonId == -1) {
            showToast("프로필 공개 여부를 선택하세요.");
            return;
        }

        // 지역 및 기타 프로필 정보를 EditUserProfile 객체에 담기
        GymLocation gymLocation = new GymLocation(latitude, longitude, selectedRegion);
        EditUserProfile profile = new EditUserProfile(
                selectedGoal,
                Integer.parseInt(workoutExperience),
                introduction,
                gymLocation,
                isProfileVisible,
                favWorkouts
        );

        MultipartBody.Part imagePart = createImagePart();
        RequestBody profileRequest = createProfileRequest(profile);

        updateProfileApiCall(profileRequest, imagePart);
    }

    private MultipartBody.Part createImagePart() {
        if (uri != null) {
            String path = getRealPathFromURI(uri);
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                } else {
                    Log.e("FileError", "File not found: " + path);
                    showToast("이미지 파일을 찾을 수 없습니다.");
                }
            } else {
                Log.e("FileError", "Unable to get path from URI: " + uri);
                showToast("이미지 경로를 가져올 수 없습니다.");
            }
        }
        return null;
    }


    private RequestBody createProfileRequest(EditUserProfile profile) {
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

    private void updateProfileApiCall(RequestBody profileRequest, MultipartBody.Part imagePart) {
        ApiService profileService = RetrofitClient.getApiService(BASE_URL);

        Call<EditProfileResponse> call = profileService.updateProfile(1, imagePart, profileRequest);
        call.enqueue(new Callback<EditProfileResponse>() {
            @Override
            public void onResponse(Call<EditProfileResponse> call, Response<EditProfileResponse> response) {
                if (response.isSuccessful()) {
                    showToast("프로필이 업데이트되었습니다.");
                } else {
                    showToast("업데이트 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<EditProfileResponse> call, Throwable t) {
                showToast("네트워크 오류: " + t.getMessage());
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("권한이 승인되었습니다.");
            } else {
                showToast("권한이 거부되었습니다. 이 기능을 사용하려면 권한이 필요합니다.");
            }
        }
    }


    /*
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }
     */

    private String getRealPathFromURI(Uri uri) {
        String path = null;

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(columnIndex);
                cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }

        return path;
    }

    private void requestPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
