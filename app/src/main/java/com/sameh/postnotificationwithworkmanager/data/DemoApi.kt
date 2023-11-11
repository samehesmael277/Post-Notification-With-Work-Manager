package com.sameh.postnotificationwithworkmanager.data

import retrofit2.http.GET

interface DemoApi {

    @GET("posts")
    suspend fun getPosts(): ArrayList<Post>
}