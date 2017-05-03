package co.sharechattest.api.service;

import co.sharechattest.api.model.FetchResponse;
import co.sharechattest.api.model.PostDataFetch;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Varun on 02/05/17.
 */

public interface PostApiInterface {

    @POST("/data")
    Call<FetchResponse> fetchTrendingFeeds(@Body PostDataFetch user);

    @POST("/update")
    Call<FetchResponse> updateAuthorInfo(@Body PostDataFetch user);

}
