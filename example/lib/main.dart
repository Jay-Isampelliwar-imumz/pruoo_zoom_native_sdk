
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:zoom_native_sdk/zoom_native_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _zoomNativelyPlugin = ZoomNativeSdk();
  bool isInitialized = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    requestPermissions();
  }

  Future<void> requestPermissions() async {
    Map<Permission, PermissionStatus> statuses = await [
      Permission.camera,
      Permission.microphone,
      Permission.storage,
      Permission.location,
      Permission.notification,
    ].request();
    print(statuses);
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.

    try {
      if (!isInitialized) {
        isInitialized = (await _zoomNativelyPlugin.initZoom(
            token:
            "")) ??
            false;
      }
    } on PlatformException catch (e) {
      debugPrint(e.message);
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: ElevatedButton(
            onPressed: () async {
              joinMeeting();
            },
            child: const Text("join"),
          ),
        ),
      ),
    );
  }

  void joinMeeting() async {
    try {
      debugPrint("joinMeting -> isInitialized = $isInitialized");
      if (isInitialized) {
        await _zoomNativelyPlugin.joinMeting(
          meetingNumber: "93993906817".replaceAll(" ", ""),
          meetingPassword: "j0Uygt",
        );
      }
    } on PlatformException catch (e) {
      print(e.toString());
    } catch (e) {
      print(e.toString());
    }
  }
}