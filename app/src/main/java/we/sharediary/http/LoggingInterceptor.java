package we.sharediary.http;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LoggingInterceptor implements Interceptor {
    public static final String TAG = "DIARY";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.i(TAG, request.url().toString());
        Response response = chain.proceed(request);
        Log.i(TAG, response.message());
        return response;
    }
}
