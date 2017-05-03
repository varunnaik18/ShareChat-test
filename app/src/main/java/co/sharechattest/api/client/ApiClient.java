package co.sharechattest.api.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sharechattest.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Varun on 02/05/17.
 */

public class ApiClient {

    private static ApiClient sInstance = null;


    private OkHttpClient okHttpClient;
    private String mBaseURL;

    private ApiClient() {

        mBaseURL = BuildConfig.BASE_URL;

        okHttpClient = getOkHttpClient();
    }

    public static ApiClient getInstance() {

        if (sInstance == null)
            sInstance = new ApiClient();

        return sInstance;
    }

    public <T> T getService(final Class<T> service) {

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mBaseURL)
                .build();

        return retrofit.create(service);
    }

    private OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {

                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        return chain.proceed(getBuildRequest(chain.request()));
                    }
                })
                .addInterceptor(loggingInterceptor)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    private Request getBuildRequest(Request originalRequest) {

        Request.Builder requestBuilder = originalRequest.newBuilder();

        requestBuilder.addHeader("Accept", "application/json");

        return requestBuilder.build();
    }

}
