package com.aboelola.zoom_native_sdk

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import us.zoom.sdk.*


/** ZoomNativeSdkPlugin */
class ZoomNativeSdkPlugin : FlutterPlugin, MethodChannel.MethodCallHandler, MeetingServiceListener,
    ZoomSDKInitializeListener, ActivityAware {

    private val TAG : String = "Pruoo_Zoom_SDK";

    private lateinit var channel: MethodChannel
    private var activity: Activity? = null

    private val WEB_DOMAIN = "zoom.us"

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "pruoo_zoom_native_sdk")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) = when (call.method) {
        "initZoom" -> {
            val arguments: Map<String, String>? = call.arguments<Map<String, String>>()
            initializeSdk(arguments?.get("token"))
            result.success(true)
        }
        "joinMeeting" -> {
            val arguments: Map<String, String>? = call.arguments<Map<String, String>>()
            joinMeeting(arguments?.get("meetingNumber"), arguments?.get("meetingPassword"))
            Log.d("TAG", "onMethodCall: $arguments")
            result.success(true)
        }
        else -> {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun initializeSdk(jwtToken: String?) {
        try {
            val sdk = ZoomSDK.getInstance()
            val params = ZoomSDKInitParams()
            Log.d(TAG, "$jwtToken")
            params.jwtToken = jwtToken
            params.domain = WEB_DOMAIN
            params.enableLog = true
            val listener: ZoomSDKInitializeListener = object : ZoomSDKInitializeListener {
                override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
                    if (errorCode == ZoomError.ZOOM_ERROR_SUCCESS) {
                        Log.d(TAG, "Successfully Initialize SDK")
                    } else {
                        Log.e(TAG, "Error while Initialization SDK CODE: $errorCode ", )
                    }
                }

                override fun onZoomAuthIdentityExpired() {

                }
            }
            sdk.initialize(activity, this, params)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Zoom SDK $e")
        }
    }


    //    private fun joinMeeting( meetingId: String?, meetingPassword: String?) {
//
//        try {
//            val meetingService = ZoomSDK.getInstance().meetingService
//
//            if (!ZoomSDK.getInstance().isInitialized){
//                Log.d(TAG, "SDK not initialized")
//            }
//            else {
//                Log.d(TAG, "SDK initialized")
//                Log.d(TAG, "$meetingId")
//                Log.d(TAG, "$meetingPassword")
//
//            }
//
//            val options = JoinMeetingOptions()
//            val params = JoinMeetingParams()
////            val opts = JoinMeetingOptions()
////            opts.no_invite = true
////            opts.no_driving_mode = false
////            opts.no_dial_in_via_phone = true
////            opts.no_disconnect_audio = false
////            opts.no_audio = false
////            opts.no_titlebar = true
////            val view_options = true
////            if (view_options) {
////                opts.meeting_views_options =
////                    MeetingViewsOptions.NO_TEXT_MEETING_ID + MeetingViewsOptions.NO_TEXT_PASSWORD
////            }
//
//            params.displayName = "JAY ISAMPELLIWAR"
//            params.meetingNo = meetingId
//            params.password = meetingPassword
//            meetingService.joinMeetingWithParams(activity, params, options)
//            Log.d(TAG, "Joining Meeting")
//        }
//        catch (e: Exception) {
//            Log.e("ZoomSDK", "joinMeeting: ${e.toString()}", )
//        }
//    }
    private fun joinMeeting(meetingId: String?, meetingPassword: String?) {
        // Some error is happing in this fun
        try {
            val meetingService = ZoomSDK.getInstance().meetingService
            val opts = JoinMeetingOptions()

            opts.no_invite = true
            opts.no_driving_mode = false
            opts.no_dial_in_via_phone = true
            opts.no_disconnect_audio = false
            opts.no_audio = false
            opts.no_titlebar = true
            val view_options = true
            if (view_options) {
                opts.meeting_views_options =
                    MeetingViewsOptions.NO_TEXT_MEETING_ID + MeetingViewsOptions.NO_TEXT_PASSWORD
            }

            val params = JoinMeetingParams()
            params.meetingNo = meetingId
            params.password = meetingPassword

            meetingService?.joinMeetingWithParams(activity, params, opts)
        } catch (e :Exception){
            Log.e(TAG, "Failed to Join Meeting $e")
        }
    }

    override fun onMeetingStatusChanged(p0: MeetingStatus?, p1: Int, p2: Int) {
        Log.d(TAG, "onMeetingStatusChanged: $p0")
    }

    override fun onMeetingParameterNotification(p0: MeetingParameter?) {
        Log.d(TAG, "onMeetingParameterNotification: $p0")

    }

    override fun onZoomSDKInitializeResult(p0: Int, p1: Int) {
        Log.d(TAG, "onZoomSDKInitializeResult: $p0 , $p1")
    }

    override fun onZoomAuthIdentityExpired() {
        Log.d(TAG, "onZoomAuthIdentityExpired")
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        channel.setMethodCallHandler(null);
    }
}