package org.techtown.schooler.Channels.ChannelHandling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.techtown.schooler.MainActivity;
import org.techtown.schooler.R;
import org.techtown.schooler.StartMemberActivity.LoginActivity;
import org.techtown.schooler.network.NetRetrofit;
import org.techtown.schooler.network.response.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author 이영은
 */

public class UploadChannelsThumbnail extends AppCompatActivity {

    private String fileExt;
    private String fileType;

    private File tempFile;
    private String uploadName;
    private String channel_id = null;

    private final int PICK_FROM_ALBUM = 1;
    private final int REQUEST_IMAGE_CROP = 2;
    SharedPreferences Login;

    private Boolean isPermission = false;

    private ImageView backgroundImage;
    private TextView Button;;
    private TextView title;
    private TextView content;
    private TextView user;
    private ImageView isPublicImage;

    private String channel_name = null;
    private String create_user = null;
    private String channel_explain = null;
    private String channel_isPublic = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_channels_thumbnail);


        Login = getSharedPreferences("Login", MODE_PRIVATE);//SharedPreferences 선언
        backgroundImage = (ImageView)findViewById(R.id.channel_Image);
        title = (TextView)findViewById(R.id.schedule_title);
        content = (TextView)findViewById(R.id.Content_TextView);
        user = (TextView)findViewById(R.id.date);
        isPublicImage = (ImageView)findViewById(R.id.isPublic_ImageView);
        Button = (TextView)findViewById(R.id.finish);


        channel_name = getIntent().getStringExtra("channel_name");
        create_user = getIntent().getStringExtra("create_user");
        channel_explain = getIntent().getStringExtra("channel_explain");
        channel_isPublic = getIntent().getStringExtra("channel_isPublic");
        channel_id = getIntent().getStringExtra("channel_id");

        backgroundImage.setBackground(new ShapeDrawable(new OvalShape()));
        backgroundImage.setClipToOutline(true);

        if (getIntent().getStringExtra("channel_thumbnail")!=null){
            String backgroundImageLink = getIntent().getStringExtra("channel_thumbnail");
            Glide.with(UploadChannelsThumbnail.this).load(backgroundImageLink).into(backgroundImage);
        }else{
            String backgroundImageLink_default= "http://10.80.163.154:5000/static/image/basic_thumbnail.jpg";
            Glide.with(UploadChannelsThumbnail.this).load(backgroundImageLink_default).into(backgroundImage);
        }

        title.setText(channel_name);
        content.setText(channel_explain);
        user.setText("Master : " + create_user);


        if(channel_isPublic.equals("0")) {
            isPublicImage.setImageResource(R.drawable.locked);
        }else{
            isPublicImage.setImageResource(R.drawable.un_locked);
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

    //엘범으로 이동
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,  PICK_FROM_ALBUM);
    }

    //권한 확인
    public void tedPermission(View view) {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 성공
                isPermission = true;
                goToAlbum();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 실패
                isPermission = false;
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(R.string.permission_1)
                .setDeniedMessage(R.string.permission_2)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    //아이템 선택 시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    //사진 선택 시 uri를 업로드이미지로 보냄.
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
        {            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("TAG", tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM:
                Uri photoUri = data.getData();
                Log.d("TAG", "PICK_FROM_ALBUM photoUri : " + photoUri);

                Cursor cursor = null;

                try {

                    /*
                     *  Uri 스키마를
                     *  content:/// 에서 file:/// 로  변경한다.
                     */
                    String[] proj = { MediaStore.Images.Media.DATA };

                    assert photoUri != null;
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    tempFile = new File(cursor.getString(column_index));

                    Log.d("TAG", "tempFile Uri : " + Uri.fromFile(tempFile));

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }


                Button.setEnabled(true);
                Button.setBackgroundResource(R.color.mainColor);

                backgroundImage.setImageURI(Uri.fromFile(tempFile));

                break;


            case REQUEST_IMAGE_CROP:

                break;
        }
    }

    //버튼 클릭 이벤트
    public void Click_Finsih(View view) {
        if (tempFile != null)
            uploadImage(changeToBytes(), tempFile.getName());
    }

    //uri를 패스로 변환해서, 서버통신 시작.
    public void uploadImage(byte[] imageBytes, String originalName) {
        String[] filenameArray = originalName.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        fileType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        fileExt = "." + extension;
        uploadName = Integer.toString(new Random().nextInt(999999999));
        RequestBody requestFile = RequestBody.create(MediaType.parse(Objects.requireNonNull(fileType)), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("thumbnail", uploadName + fileExt, requestFile);
        RequestBody fileNameBody = RequestBody.create(MediaType.parse("text/plain"), uploadName);

        Call<Response> res = NetRetrofit.getInstance().getChannel().uploadThumbnail(Login.getString("token", ""), body,fileNameBody, channel_id);
        res.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.code() == 200){
                    Log.e("UploadImage", "성공"+response);
                    startActivity(new Intent(UploadChannelsThumbnail.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else if(response.code() == 410){
                    SharedPreferences.Editor editor = Login.edit();
                    editor.putString("token",null);
                    editor.putString("id",null);
                    editor.commit();
                    startActivity(new Intent(UploadChannelsThumbnail.this, LoginActivity.class));
                    Log.e("","토큰 만료");
                    Toast.makeText(UploadChannelsThumbnail.this, R.string.tokenMessage_1, Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("","오류 발생");
                    Toast.makeText(UploadChannelsThumbnail.this, R.string.serverErrorMessage_1, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("","네트워크 오류");
                Toast.makeText(UploadChannelsThumbnail.this, R.string.networkErrorMessage_1, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toGoBack (View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
