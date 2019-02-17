package com.br.myfavoritehero.data.request

import com.br.myfavoritehero.BuildConfig
import com.br.myfavoritehero.base.BaseResponse
import com.br.myfavoritehero.data.models.Hero
import com.br.myfavoritehero.data.network.ApiService
import com.br.myfavoritehero.util.Constants.READ_TIMEOUT
import com.br.myfavoritehero.util.Util
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.br.myfavoritehero.util.Constants.CONNECTION_TIMEOUT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class Repository{

    private var apiService: ApiService

    init {
        val httpLogInterceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            httpLogInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLogInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        val retrofit = Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .baseUrl(BuildConfig.BASE_URL)
            .client(OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(httpLogInterceptor)
                .build())
            .build()


        apiService = retrofit.create<ApiService>(ApiService::class.java)
    }

    fun getHeroes(
        success: (base: BaseResponse<Hero>) -> Unit,
        error: (t: Throwable) -> Unit
    ){
        val ts = (System.currentTimeMillis() / 1000)
        val hash = Util.md5(ts.toString() + BuildConfig.PRIVATE_KEY + BuildConfig.PUBLIC_KEY)

        val call = apiService.getHeroes(ts, BuildConfig.PUBLIC_KEY, hash)
        Timber.d("URL: ${call.request().url()}")
        call.enqueue(
            object: Callback<BaseResponse<Hero>> {
                override fun onFailure(call: Call<BaseResponse<Hero>>, t: Throwable) {
                    error(t)
                }

                override fun onResponse(
                    call: Call<BaseResponse<Hero>>,
                    baseResponse: retrofit2.Response<BaseResponse<Hero>>
                ) {
                    if(baseResponse.code() == 200) {
                        success(baseResponse.body()!!)
                    }else{
                       error(Throwable())
                    }
                }
            }
        )
    }

    fun loadMore(
        success: (base: BaseResponse<Hero>) -> Unit,
        error: (t: Throwable) -> Unit,
        offset: Int
    ){
        val ts = (System.currentTimeMillis() / 1000)
        val hash = Util.md5(ts.toString() + BuildConfig.PRIVATE_KEY + BuildConfig.PUBLIC_KEY)

        val call = apiService.getHeroes(ts, BuildConfig.PUBLIC_KEY, hash, 50, offset)
        Timber.d( "URL: ${call.request().url()}")
        call.enqueue(
            object: Callback<BaseResponse<Hero>> {
                override fun onFailure(call: Call<BaseResponse<Hero>>, t: Throwable) {
                    error(t)
                }

                override fun onResponse(
                    call: Call<BaseResponse<Hero>>,
                    baseResponse: retrofit2.Response<BaseResponse<Hero>>
                ) {
                    if(baseResponse.code() == 200) {
                        success(baseResponse.body()!!)
                    }else{
                        error(Throwable())
                    }
                }
            }
        )
    }

}
