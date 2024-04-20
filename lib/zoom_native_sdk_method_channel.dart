import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'zoom_native_sdk_platform_interface.dart';

/// An implementation of [ZoomNativeSdkPlatform] that uses method channels.
class MethodChannelZoomNativeSdk extends ZoomNativeSdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('pruoo_zoom_native_sdk');

  @override
  Future<bool?> initZoom({
    required String token,

  }) async {
    final version = await methodChannel.invokeMethod<bool>(
      'initZoom',
      {
        "token": token,
      },
    );
    return version;
  }

  @override
  Future<bool?> joinMeting({
    required String meetingNumber,
    required String meetingPassword,
  }) async {
    final version = await methodChannel.invokeMethod<bool>(
      'joinMeeting',
      {
        "meetingNumber": meetingNumber,
        "meetingPassword": meetingPassword,
      },
    );
    debugPrint("MethodChannelZoomNatively-joinMeting -> $version");
    return version;
  }
}
