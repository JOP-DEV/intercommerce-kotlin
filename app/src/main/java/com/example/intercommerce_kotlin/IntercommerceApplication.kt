package com.example.intercommerce_kotlin

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class IntercommerceApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val thirtyDaysSeconds = TimeUnit.DAYS.toSeconds(30)

        val imageCacheClient = OkHttpClient.Builder()
            .cache(
                Cache(
                    directory = cacheDir.resolve("coil_http_cache"),
                    maxSize = 100L * 1024L * 1024L
                )
            )
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                val contentType = response.header("Content-Type").orEmpty()
                if (contentType.startsWith("image/", ignoreCase = true)) {
                    response.newBuilder()
                        .header("Cache-Control", "public, max-age=$thirtyDaysSeconds")
                        .build()
                } else {
                    response
                }
            }
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(imageCacheClient)
            .respectCacheHeaders(false)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_disk_cache"))
                    .maxSizeBytes(250L * 1024L * 1024L)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .build()
    }
}
