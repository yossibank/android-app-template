# android-app-template

Android アプリの初期テンプレート。Jetpack Compose の 1 画面のみを含む最小構成。

## 環境

| 項目 | バージョン |
| --- | --- |
| Gradle | 9.7.1 |
| Android Gradle Plugin | 9.3.2 |
| compileSdk / targetSdk | 37 |
| minSdk | 24 |
| Compose BOM | 2026.08.00 |

## ビルド

```sh
make verify   # デバッグ APK のビルド + ユニットテスト
make build    # デバッグ APK のみ
make test     # ユニットテストのみ
```

Android Studio 用の Run Configuration「All Tests」を `.run/` に用意している。

## 関連リポジトリ

- [ios-app-template](https://github.com/yossibank/ios-app-template)
- [kmp-app-template](https://github.com/yossibank/kmp-app-template) — 共通ロジック（KMP）
