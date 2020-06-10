package org.techtown.schooler.NavigationFragment;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.techtown.schooler.Account.JoinedChannel;
import org.techtown.schooler.MainActivity;
import org.techtown.schooler.Model.UserInfo;
import org.techtown.schooler.R;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment {

    SharedPreferences Login;
    UserInfo userData;

    // View
    ImageView profile;
    Button edit_profile;
    ImageButton check_channel;
    TextView user_name;
    TextView user_id;
    TextView user_phone;
    TextView user_email;
    TextView user_school;
    TextView user_class;
    LinearLayout channel_layout;

    private Boolean isPermission = true;
    private static final int PICK_FROM_ALBUM = 1;
    private final int REQUEST_IMAGE_CROP = 2;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    private String fileExt;
    private String fileType;
    private String uploadName;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_account, container, false);
        Login = getActivity().getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언

        userProfile();

        // View
        profile = rootView.findViewById(R.id.profile);
        edit_profile = rootView.findViewById(R.id.edit_profile); // Button
        check_channel = rootView.findViewById(R.id.check_channel); // Button
        user_name = rootView.findViewById(R.id.user_name);
        user_id = rootView.findViewById(R.id.user_id);
        user_phone = rootView.findViewById(R.id.user_phone);
        user_email = rootView.findViewById(R.id.user_email);
        user_school = rootView.findViewById(R.id.user_school);
        user_class = rootView.findViewById(R.id.user_class);
        channel_layout = rootView.findViewById(R.id.channel_layout);

        // profile 즉 프로필 사진을 둥글게 만들어줍니다.
        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자로부터 권한 요청을 받는 tedPermission() 매서드를 호출
                tedPermission();
            }
        });

        check_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), JoinedChannel.class);
                startActivity(intent);

                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        channel_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), JoinedChannel.class);
                startActivity(intent);

                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return rootView;
    }

    public void userProfile(){

        Call<Response<Data>> res = NetRetrofit.getInstance().getProfile().GetProfile(Login.getString("token",""),"");
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {

                if(response.code() == 200){

                    userData = response.body().getData().getUserInfo();

                    // View 텍스트 삽입
                    user_name.setText(userData.getUser_name());
                    user_id.setText("(" + userData.getUser_id() + ")");

                    if(userData.getUser_phone() == ""){
                        user_phone.setText("전화번호 없음");
                    } else{
                        user_phone.setText(userData.getUser_phone());
                    }
                    user_email.setText(userData.getUser_email());
                    user_school.setText(userData.getSchool().getSchool_name());

                    if(userData.getSchool_grade() == null){
                        user_class.setText("학반 정보 없음");
                    } else{
                        user_class.setText(userData.getSchool_grade() + "학년 " + userData.getSchool_class() + "반");
                    }

                    Glide.with(getActivity()).load(userData.getProfile_pic()).into(profile);

                    Toast.makeText(getActivity(), "정상적으로 프로필을 조회하였습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("[status 200]",response.message());

                } else if(response.code() == 403){
                    Log.e("[status 403]",response.message());
                } else if(response.code() == 404){

                    Log.e("[status 404]",response.message());
                } else if(response.code() == 500){

                    Log.e("[status 500]",response.message());
                }

            }

            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {

                Toast.makeText(getActivity(), "서버 통신 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("[서버 통신 X]","서버 통신 오류");
            }
        });

    }

    public void tedPermission(){

        // PermissionListener 클래스 참조
        PermissionListener permissionListener = new PermissionListener() {

            // 권한 요청 성공
            @Override
            public void onPermissionGranted() {

                // 사용자가 승락을 한 경우
                if(isPermission == true) {
                    goToAlbum();
                }
            }

            // 권한 요청 실패
            // 사용자가 거절한 경우
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };

        // 권한 요청 메세지 내용
        TedPermission.with(getContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2)) // 안내 문자 1
                .setDeniedMessage(getResources().getString(R.string.permission_1)) // 안내 문자 2
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    // 앨범 호출 함수로 부터 전달 받는 onActivityResult 함수
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        switch (requestCode) {

            // goToAlbum 매서드에서 startActivityForResult result_code 로 전달하는 값
            case PICK_FROM_ALBUM:

                // Uri 클래스를 사용해서 photoUri 변수에 값을 저장받는다.
                Uri photoUri = null;

                try{
                    photoUri = data.getData();
                }catch (NullPointerException e){

                    photoUri = null;
                }

                Log.d("TAG", "PICK_FROM_ALBUM photoUri : " + photoUri);

                // cursor 변수
                Cursor cursor = null;

                try {
                    // proj 배열에 갤러리로 부터 받은 값을 저장한다.
                    String[] proj = { MediaStore.Images.Media.DATA };

                    // assert 자료형을 사용합니다.
                    // assert 자료형은 null 이 들어오면 안되는 값에 사용한다.
                    assert photoUri != null;
                    cursor = getActivity().getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    tempFile = new File(cursor.getString(column_index));

                    Log.d("TAG", "tempFile Uri : " + Uri.fromFile(tempFile));

                }catch (NullPointerException e){

                }

                finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }


                try {

                    profile.setImageURI(Uri.fromFile(tempFile));
                    uploadProfile(changeToBytes(), tempFile.getName());
                } catch (NullPointerException e){
                    
                }

                break;

            case REQUEST_IMAGE_CROP:

                break;
        }
    }

    //이미지파일을 비트로 바꿉니다
    private byte[] changeToBytes() {

        int size = (int) tempFile.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(tempFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    // 앨범 호출 함수
    public void goToAlbum(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        // startActivityForResult 매서드를 호출하면서 매개변수로 PICK_FROM_ALBUM 변수를 전달 합니다.
        // result_code 이다.
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public static class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("[ImageDownLoad][Error]", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public void uploadProfile(byte[] imageBytes, String originalName){

        String[] filenameArray = originalName.split("\\.");
        String extension = filenameArray[filenameArray.length -1];

        fileType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        fileExt = "." + extension;

        uploadName = Integer.toString(new Random().nextInt(999999999));

        RequestBody requestFile = RequestBody.create(MediaType.parse(Objects.requireNonNull(fileType)), imageBytes);

        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_pic", uploadName + fileExt, requestFile);

        RequestBody fileNameBody = RequestBody.create(MediaType.parse("text/plain"), uploadName);

        Call<Response<Data>> res  = NetRetrofit.getInstance().getProfile().uploadProfile(Login.getString("token",""), body, fileNameBody);
        res.enqueue(new Callback<Response<Data>>() {
            @Override
            public void onResponse(Call<Response<Data>> call, retrofit2.Response<Response<Data>> response) {
                if(response.code() ==200)
                {
                    Log.e("[upload]", "성공");

                    getActivity().finish();
                    startActivity(getActivity().getIntent().putExtra("profile", true));

                }
            }
            @Override
            public void onFailure(Call<Response<Data>> call, Throwable t) {

                Log.e("[upload]", "실패");
            }
        });
    }
}
