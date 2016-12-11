package we.sharediary.http;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import rx.Observable;
import we.sharediary.result.CityInfoResult;

/**
 * Created by Jayden on 2016/1/10.
 */
public class HttpManager {
    public static final String HOST = "http://apis.baidu.com/apistore/";
    static Retrofit retrofit;
    public static HttpService getService(){
        if (retrofit == null){
            OkHttpClient client = new OkHttpClient();
            client.setReadTimeout(30, TimeUnit.SECONDS);
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setWriteTimeout(30, TimeUnit.SECONDS);
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(httpLoggingInterceptor);
            retrofit = new Retrofit.Builder()
                    .baseUrl(HOST)
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(HttpService.class);
    }

    public interface  HttpService{
        @GET("weatherservice/cityname")
        Observable<CityInfoResult> getCityInfo(@Query("cityname") String cityname, @Header("apikey") String apikey);
    }

}
