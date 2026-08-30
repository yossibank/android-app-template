# android-app-template

Android アプリ。Jetpack Compose の 1 画面のみの最小構成。

## 検証

変更したら必ず通す。通らないものは完了ではない。

```sh
make verify   # assembleDebug + ユニットテスト
```

## 規約

- UI は Jetpack Compose のみ。View / XML レイアウトは追加しない。
- 共通ロジックは kmp-app-template 側に置く。ここには Android 固有のものだけ。
- バージョンは `gradle/libs.versions.toml` にのみ書く。build.gradle.kts に直書きしない。

## やってはいけない

- `org.jetbrains.kotlin.android` を適用しない（AGP 9 でエラーになる）
- Compose Compiler プラグイン（`org.jetbrains.kotlin.plugin.compose`）を外さない
- `.gitignore` に `*.jar` を追加しない（`gradle-wrapper.jar` が消える）
- `compileSdk` / `targetSdk` を 37 未満に下げない（AndroidX の最新版が要求する）
