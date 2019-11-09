package org.techtown.schooler.network;

import org.techtown.schooler.Model.SchoolList;
import org.techtown.schooler.network.retrofit.interfaces.ChannelEvent;
import org.techtown.schooler.network.retrofit.interfaces.Login;
import org.techtown.schooler.network.retrofit.interfaces.Profile;
import org.techtown.schooler.network.retrofit.interfaces.SchoolEvent;
import org.techtown.schooler.network.retrofit.interfaces.SignUp;
import org.techtown.schooler.network.retrofit.interfaces.Channel;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetRetrofit {
    private static NetRetrofit ourInstance = new NetRetrofit();
    public static NetRetrofit getInstance() {
        return ourInstance;
    }

    private NetRetrofit() {

    }

    Retrofit retrofit = new Retrofit.Builder()

//            .baseUrl("http://10.80.163.154:5000")//내부망
            .baseUrl("http://10.80.161.63:5000")//AWS
            .addConverterFactory(GsonConverterFactory.create()) // 파싱등록
            .build();

//http://10.80.162.124:5000 바인드 서버
//http://192.168.64.1:5000 진우 서버

    Login login = retrofit.create(Login.class);
    public Login getLogin() {
        return login;
    }
    //로그인 인터페이스 등록

    SignUp Signup = retrofit.create(SignUp.class);
    public SignUp getSignup() {
        return Signup;
    }
    //로그인 인터페이스 등록

    Channel Channel = retrofit.create(Channel.class);
    public Channel getChannel() {return Channel;}

    ChannelEvent channelEvent = retrofit.create(ChannelEvent.class);

    public ChannelEvent getChannelEvent() {
        return channelEvent;
    }

    SchoolEvent schoolEvent = retrofit.create(SchoolEvent.class);

    public SchoolEvent getSchoolEvent() {
        return schoolEvent;
    }

    Profile profile = retrofit.create(Profile.class);

    public Profile getProfile() {
        return profile;
    }
}

