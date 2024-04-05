package com.asianitinc.availtrade.config;

import com.asianitinc.availtrade.model.ServerResponseModel;
import com.asianitinc.availtrade.model.GetUserResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiConfig
{
    @FormUrlEncoded
    @POST("credential/api.php")
    Call<ServerResponseModel> userRegistration(@Query("registration") String registration,
                                        @Field("email") String email,
                                        @Field("password") String password,
                                        @Field("phone") String phone,
                                        @Field("dob") String dob);

    @FormUrlEncoded
    @POST("api/checkApiRequest")
    Call<ServerResponseModel> checkapirequest(@Query("checkapirequest") String checkapirequest,
                                    @Field("device") String device);

    @FormUrlEncoded
    @POST("api/importContact")
    Call<ServerResponseModel> importContact(@Query("importContact") String importContact,
                                              @Field("name") String name, @Field("mobile") String mobile, @Field("admin") String admin);

    @FormUrlEncoded
    @POST("api/android-login")
    Call<ServerResponseModel> userLogin(@Query("login") String login,
                                        @Field("username") String infoUserName,
                                        @Field("password") String infoPassWord);

    @GET("api/users")
    Call<GetUserResponseModel> getUserData(@Query("page")String page);
}
