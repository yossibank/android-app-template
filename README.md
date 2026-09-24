# android-app-template

Android アプリのテンプレート。共通ロジックは [kmp-app-template](https://github.com/yossibank/kmp-app-template) から取得する。
iOS 版は [ios-app-template](https://github.com/yossibank/ios-app-template)。

## 準備

`~/.gradle/gradle.properties` に `gpr.user` / `gpr.token` を置く（GitHub Packages から共通コアを取得するのに必要）。

## 使い方

変更したら `make verify`。
