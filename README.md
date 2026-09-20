# android-app-template

> Android アプリの初期テンプレート。Jetpack Compose の 1 画面のみを含む最小構成。

書き方の規約は [CLAUDE.md](CLAUDE.md)。

## 3 リポジトリの関係

```mermaid
flowchart LR
    KMP["kmp-app-template<br/>共通ロジック"]
    AND["android-app-template<br/>← このリポジトリ"]
    IOS["ios-app-template<br/>iOS アプリ"]
    KMP -->|"AAR / klib"| AND
    KMP -->|"Shared.xcframework"| IOS
```

[ios-app-template](https://github.com/yossibank/ios-app-template) ・
[kmp-app-template](https://github.com/yossibank/kmp-app-template)

## モジュール構成

```mermaid
flowchart LR
    SHARED["shared<br/><i>共通コア</i>"]
    VM["PokemonListViewModel"]
    SCREEN["PokemonListScreen"]
    ACT["MainActivity"]
    SHARED --> VM
    VM -->|"StateFlow&lt;PokemonListUiState&gt;"| SCREEN
    SCREEN --> ACT
```

単一モジュール（`:app`）。1 画面を 3 つのファイルに分ける。

| ファイル | 役割 |
| --- | --- |
| `PokemonListUiState.kt` | 画面の状態（読み込み中 / 一覧 / 失敗） |
| `PokemonListViewModel.kt` | 取得と状態の保持。構成変更を跨いで生き残る |
| `PokemonListScreen.kt` | 状態を持つ Composable と、描画だけの Composable。Scaffold と AppBar も持つ |
| `TextMatching.kt` | 絞り込みの一致規則。iOS の `localizedStandardContains` に合わせる |
| `MainActivity.kt` | 入口。テーマと画面の呼び出しだけ |

## ディレクトリ

```
app/
├── build.gradle.kts        # 依存とビルド設定
└── src/
    ├── main/kotlin/        # 画面
    └── test/kotlin/        # ViewModel のテスト
gradle/
└── libs.versions.toml      # 依存とバージョン（ここにのみ書く）
```

## コマンド

| コマンド | 内容 |
| --- | --- |
| `make verify` | ktlint + ビルド + ユニットテスト（変更後はこれを通す） |
| `make build` | デバッグ APK のみ |
| `make test` | ユニットテストのみ |
| `make lint` | ktlint によるチェック（`make verify` に含まれる） |
| `make format` | ktlint で自動修正 |

## 環境

バージョンはここに書き写さない。更新は Renovate が出所のファイルだけを直すので、
写した値は必ず古くなる。

| 項目 | 出所 |
| --- | --- |
| AGP・Kotlin・Compose BOM・依存 | [gradle/libs.versions.toml](gradle/libs.versions.toml) |
| Gradle | [gradle/wrapper/gradle-wrapper.properties](gradle/wrapper/gradle-wrapper.properties) |
| compileSdk / targetSdk / minSdk・Java | [app/build.gradle.kts](app/build.gradle.kts) |
| JDK（CI） | [.github/workflows/verify.yml](.github/workflows/verify.yml) |
| 認証 | `~/.gradle/gradle.properties` に `gpr.user` / `gpr.token`（共通コアの取得に必要） |
