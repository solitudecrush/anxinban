package com.elderlycare.elderly_care_app

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    companion object {
        private const val CHANNEL = "com.elderlycare.elderly_care_app/amap"
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "getApiKey" -> {
                    try {
                        val appInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                        val key = appInfo.metaData?.getString("com.amap.api.v2.apikey") ?: ""
                        result.success(key)
                    } catch (e: Exception) {
                        result.error("UNAVAILABLE", "无法读取高德 Key", e.message)
                    }
                }
                "sendSms" -> {
                    val phoneNumber = call.argument<String>("phoneNumber") ?: ""
                    val message = call.argument<String>("message") ?: ""
                    val simSlot = call.argument<Int>("simSlot") ?: 0
                    if (phoneNumber.isEmpty() || message.isEmpty()) {
                        result.error("INVALID_ARGUMENT", "手机号或短信内容不能为空", null)
                        return@setMethodCallHandler
                    }
                    try {
                        val sent = sendSmsDirect(phoneNumber, message, simSlot)
                        result.success(sent)
                    } catch (e: Exception) {
                        result.error("SMS_SEND_ERROR", e.message, e.toString())
                    }
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun sendSmsDirect(phoneNumber: String, message: String, simSlot: Int): Boolean {
        return try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager = getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                val subInfoList = subscriptionManager?.activeSubscriptionInfoList
                if (!subInfoList.isNullOrEmpty()) {
                    val targetSub = subInfoList.find { it.simSlotIndex == simSlot }
                    if (targetSub != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        SmsManager.getSmsManagerForSubscriptionId(targetSub.subscriptionId)
                    } else {
                        SmsManager.getDefault()
                    }
                } else {
                    SmsManager.getDefault()
                }
            } else {
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
