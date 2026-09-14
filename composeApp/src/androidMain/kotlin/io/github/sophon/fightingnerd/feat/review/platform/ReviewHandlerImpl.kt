package io.github.sophon.fightingnerd.feat.review.platform

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

internal class ReviewHandlerImpl(
    private val app: Application,
) : ReviewHandler {
    private val currentActivity = AtomicReference<Activity?>()


    init {
        app.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                override fun onActivityResumed(activity: Activity) {
                    currentActivity.set(activity)
                }

                override fun onActivityPaused(activity: Activity) {
                    if (currentActivity.get() == activity) currentActivity.set(null)
                }

                override fun onActivityDestroyed(activity: Activity) {
                    if (currentActivity.get() == activity) currentActivity.set(null)
                }

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
                override fun onActivityStarted(activity: Activity) = Unit
                override fun onActivityStopped(activity: Activity) = Unit
                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            }
        )
    }


    override suspend fun requestReview(): EmptyResult<AppError> {
        val activity = currentActivity.get()
            ?: return Result.Error(AppError.ReviewError("no active activity"))

        val result = try {
            withContext(Dispatchers.Main) {
                val manager = ReviewManagerFactory.create(app)
                val info = manager.requestReview()
                manager.launchReview(activity, info)
            }
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            Result.Error(AppError.ReviewError(t.message ?: t::class.simpleName ?: "unknown"))
        }

        return result
    }
}