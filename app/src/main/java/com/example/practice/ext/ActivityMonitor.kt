package com.example.practice.ext

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import java.lang.ref.WeakReference

object ActivityMonitor {

    private const val TAG = "ActivityMonitor"

    var topActivity: WeakReference<Activity?> = WeakReference(null)

    private val onEnterForegroundListenerList = mutableListOf<OnEnterForegroundListener>()

    private var resumeCounter = 0

    private var coldBootForeground = true // 标记是否为冷启那一次进入前台

    private val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            "onActivityCreated: $activity".logI(TAG)
        }

        override fun onActivityStarted(activity: Activity) {
            "onActivityStarted: $activity".logI(TAG)
        }

        override fun onActivityResumed(activity: Activity) {
            "onActivityResumed: $activity".logI(TAG)

            topActivity = WeakReference(activity)

            val coldBoot = coldBootForeground
            coldBootForeground = false

            resumeCounter++
            if (resumeCounter == 1) {
                "app enter foreground".logI(TAG)
                onEnterForegroundListenerList.forEach {
                    it.onEnterForeground(coldBoot)
                }
            }
        }

        override fun onActivityPaused(activity: Activity) {
            "onActivityPaused: $activity".logI(TAG)
            resumeCounter--
            if (resumeCounter == 0) {
                "app enter background".logI(TAG)
                onEnterForegroundListenerList.forEach {
                    it.onEnterBackground()
                }
            }
        }

        override fun onActivityStopped(activity: Activity) {
            "onActivityStopped: $activity".logI(TAG)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            "onActivitySaveInstanceState: $activity".logI(TAG)
        }

        override fun onActivityDestroyed(activity: Activity) {
            "onActivityDestroyed: $activity".logI(TAG)
        }

    }

    fun init(applicationContext: Application) {
        applicationContext.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun addOnEnterForegroundListener(listener: OnEnterForegroundListener) {
        if (onEnterForegroundListenerList.contains(listener)) {
            "addOnEnterForegroundListener: listener $listener already added".logI(TAG)
            return
        }
        onEnterForegroundListenerList.add(listener)
    }

    fun removeOnEnterForegroundListener(listener: OnEnterForegroundListener) {
        onEnterForegroundListenerList.remove(listener)
    }

    /**
     * 是否在前台
     */
    fun foreground() = resumeCounter > 0

}

interface OnEnterForegroundListener {
    /**
     * 进入前台
     */
    fun onEnterForeground(coldBoot: Boolean) = Unit

    /**
     * 进入后台
     */
    fun onEnterBackground() = Unit
}