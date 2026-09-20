# android-app-template

## プロジェクト概要

Android アプリの初期テンプレート。ビジネスロジックは Kotlin Multiplatform の共通コアに置き、
このリポジトリには Android 固有のものだけを置く。

kmp-app-template（共通ロジック）・ios-app-template と合わせた 3 リポジトリ構成の 1 つ。

## 技術スタック

| 項目 | 採用 |
| --- | --- |
| UI | Jetpack Compose |
| 状態管理 | ViewModel + StateFlow |
| 並行性 | Kotlin Coroutines |
| テスト | JUnit 4 + kotlinx-coroutines-test |
| 依存管理 | Gradle（バージョンカタログ） |

バージョンは [gradle/libs.versions.toml](gradle/libs.versions.toml)。

## プロジェクト構成

```
app/src/main/     画面。1 画面を UiState / ViewModel / Screen の 3 つに分ける
app/src/test/     ViewModel のテスト
core/src/         画面をまたいで使う仕組み。純 JVM で、共通コアにも Android にも依存しない
gradle/           依存とバージョン
```

`:app` と `:core`。ファイルの役割は [README.md](README.md)。

## 使用ライブラリ

| | |
| --- | --- |
| `com.yossibank:shared` | 共通コア。唯一の依存。GitHub Packages から取得 |
| AndroidX Lifecycle / Compose | ViewModel と UI |
| ktlint | 書式のチェック |
| Renovate | 依存の更新 PR（毎週月曜） |

## コーディング規約

- コードに無駄なコメントを書かない。

## 全体ルール

- 変更したら `make verify` を通す。通らないものは完了ではない。
- `verify` には `assembleRelease` が入っている。R8 は有効で、keep ルール無しで通る。
  外すと縮小の壊れに気づけない。
- 共通ロジックは kmp-app-template 側に置く。ここには Android 固有のものだけ。
- 同じ画面が ios-app-template にもある。挙動を変えるときは向こうに合わせる。
  絞り込みの一致規則は `standardContains`（大文字小文字と発音記号を無視）で、
  iOS の `localizedStandardContains` と揃えてある。素の `contains` を使わない。
- 失敗画面と絞り込み 0 件の見せ方は各 OS の作法に寄せる。揃えるのは文言の語彙まで。
- androidTest は無効化している。インストルメンテーションテストを書くなら
  `app/build.gradle.kts` の `enableAndroidTest` を戻す。
- バージョンを `gradle/libs.versions.toml` 以外で指定しない。
- `org.jetbrains.kotlin.android` を適用しない（AGP 9 でエラーになる）。
- `.gitignore` に `*.jar` を追加しない（`gradle-wrapper.jar` が消える）。
