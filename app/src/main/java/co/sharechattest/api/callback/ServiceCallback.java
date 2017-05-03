package co.sharechattest.api.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Varun on 02/05/17.
 */

public abstract class ServiceCallback<T> implements Callback<T> {

    private static final String TAG = ServiceCallback.class.getSimpleName();

    protected abstract void onSuccess(T response);

    protected abstract void onFailure(int code, String message, T response);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        if (response.code() >= 200 && response.code() < 400) {
            onSuccess(response.body());
            return;
        }

        onFailure(response.code(), response.message(), response.body());
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        onFailure(0, "", null);
    }

}
