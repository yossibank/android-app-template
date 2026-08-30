# android-app-template

Android アプリの初期テンプレート。Jetpack Compose の 1 画面のみを含む最小構成。

## 構成

| 項目 | バージョン |
| --- | --- |
| Gradle | 9.7.1 |
| Android Gradle Plugin | 9.3.2 |
| Kotlin | AGP 9 の組み込み Kotlin に委譲 |
| compileSdk / targetSdk | 37 |
| minSdk | 24 |
| Compose BOM | 2026.08.00 |

AGP 9.0 以降は Kotlin サポートが AGP に組み込まれたため、`org.jetbrains.kotlin.android`
プラグインは適用しない。Compose Compiler プラグイン
(`org.jetbrains.kotlin.plugin.compose`) は引き続き必要。

## ビルド

```sh
./gradlew assembleDebug        # デバッグ APK
./gradlew testDebugUnitTest    # ユニットテスト
```

## 関連リポジトリ

- [ios-app-template](https://github.com/yossibank/ios-app-template)
- [kmp-app-template](https://github.com/yossibank/kmp-app-template) — 共通ロジック（KMP）
