# android-app-template

Android アプリ。Jetpack Compose の 1 画面のみの最小構成。

## 検証

共通コアは GitHub Packages から取得する。`~/.gradle/gradle.properties` に
`gpr.user` と `gpr.token` が必要（GitHub Packages は public リポジトリでも読み取りに
トークンを要求する）。CI では `GITHUB_ACTOR` / `GITHUB_TOKEN` が自動で使われる。

変更したら必ず通す。通らないものは完了ではない。

```sh
make verify   # ktlint + assembleDebug
```

## 規約

- UI は Jetpack Compose のみ。View / XML レイアウトは追加しない。
- 共通ロジックは kmp-app-template 側に置く。ここには Android 固有のものだけ。
- ロジックのテストは kmp-app-template の `commonTest` に置く。1 度書けば両OSで走る。
  ここに置くのは Android 固有のテストだけで、現在は 0 件。テスト用の依存も持たない。
- バージョンは `gradle/libs.versions.toml` にのみ書く。build.gradle.kts に直書きしない。

## やってはいけない

- `org.jetbrains.kotlin.android` を適用しない（AGP 9 でエラーになる）
- Compose Compiler プラグイン（`org.jetbrains.kotlin.plugin.compose`）を外さない
- `.gitignore` に `*.jar` を追加しない（`gradle-wrapper.jar` が消える）
- `compileSdk` / `targetSdk` を 37 未満に下げない（AndroidX の最新版が要求する）
- `INTERNET` 権限を宣言しない。共通コアの `ktor-client-okhttp` が引き込む
  `okhttp-android` が宣言しており、release のマージ後マニフェストにも入ることを確認済み。
  共通コアが HTTP クライアントを別のエンジンに替えたら、ここで宣言し直すこと。
