# ThirdpartyPhysicalChannelConfig

システム権限で保護されている`PhysicalChannelConfig`を、`Shizuku-API`を利用して取得してみる。  
これにより、公開APIでは取得できなかった **5GのNSA 状態** とか **4Gのキャリアアグリゲーション** 状態が取れるかも、、、？

# つかいかた

- 端末で Shizuku アプリをインストールし、 ADB / ワイヤレスデバッグ のどちらかを利用して、Shizuku を実行状態にします
- Android Studio で このリポジトリを開く
- android.jar を差し替える
  - https://github.com/Reginer/aosp-android-jar
  - ↑ の手順で android.jar を差し替えます
  - 差し替えないとビルドできません
- 実行する
- Shizuku 権限を付与する
- 以上

# 開発を始める前に

## https://github.com/RikkaApps/Shizuku-API
ADB ( Shell ) 権限で任意の Android API を呼び出すことができます。
これは、一部のサードパーティがアクセスできない権限で保護されている システムレベルのAPI へのアクセスを提供してくれます。（システムレベルの権限がシェルの場合許可されるのを利用？）
今回は [android.telephony.TelephonyCallback.PhysicalChannelConfigListener] をサードパーティのアプリで利用するため、Shizuku-API を利用します。

## https://github.com/Reginer/aosp-android-jar
Android Studio からダウンロードできる android.jar は 隠されている API は削除された状態でリリースされます。（一般アプリでは呼び出せないため）
しかし今回は Shizuku-API でシステムが利用するAPIを利用したいため使います。

## https://github.com/LSPosed/AndroidHiddenApiBypass
隠されている API へアクセスします。
隠されているクラス、メソッドは、リフレクションを使って無理やりアクセスしようとしても制限されているため、例外を投げます。
この制限を解除して使えるようにしたものがこちらです。
システム権限が必要なAPIをこちらを使って解除し、Shizuku-APIを組み合わせて昇格した権限でクラス、メソッドへアクセスします。
