package we.sharediary.http;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import we.sharediary.result.CityInfoResult;

/**
 * Created by Jayden on 2016/1/10.
 */
public class HttpManager {
    public static final String HOST = "https://api.seniverse.com/";
    static Retrofit retrofit;
    public static HttpService getService(){
        if (retrofit == null){
//            OkHttpClient client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new LoggingInterceptor())
                    .build();
//            client.setReadTimeout(30, TimeUnit.SECONDS);
//            client.setConnectTimeout(30, TimeUnit.SECONDS);
//            client.setWriteTimeout(30, TimeUnit.SECONDS);
//            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            client.interceptors().add(httpLoggingInterceptor);
            retrofit = new Retrofit.Builder()
                    .baseUrl(HOST)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(HttpService.class);
    }

    public interface  HttpService{
        @GET("v3/weather/now.json")
        Observable<CityInfoResult> getCityInfo(@Query("location") String cityname, @Query("key") String apikey,
                                               @Query("language") String language, @Query("unit") String unit);
    }

}
