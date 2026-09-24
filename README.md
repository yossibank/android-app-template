<div align="center">

# android-app-template

Jetpack Compose と Kotlin Multiplatform で作る、ポケモン図鑑アプリのテンプレート

[![Verify](https://github.com/yossibank/android-app-template/actions/workflows/verify.yml/badge.svg)](https://github.com/yossibank/android-app-template/actions/workflows/verify.yml)
[![License](https://img.shields.io/github/license/yossibank/android-app-template)](LICENSE)

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Coroutines](https://img.shields.io/badge/Coroutines-7F52FF?logo=kotlin&logoColor=white)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin_Multiplatform-7F52FF?logo=kotlin&logoColor=white)

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="docs/images/demo-dark.gif">
  <img src="docs/images/demo.gif" width="260" alt="スクロールで続きを読み込み、タイプで絞り込む">
</picture>
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="docs/images/list-dark.png">
  <img src="docs/images/list-light.png" width="260" alt="ポケモンの一覧">
</picture>

</div>

PokeAPI のポケモンを、無限スクロール・タイプでの絞り込み・並び替え・詳細シートで見られます。データの取得とページングは共通コア（Kotlin Multiplatform）が担い、このリポジトリは Android の画面と状態管理だけを持ちます。

## 3 つのリポジトリ

```mermaid
flowchart LR
    KMP["kmp-app-template<br/>共通ロジック"]
    AND["android-app-template<br/>Android アプリ"]
    IOS["ios-app-template<br/>iOS アプリ"]
    KMP -->|"AAR / klib"| AND
    KMP -->|"Shared.xcframework"| IOS
```

[kmp-app-template](https://github.com/yossibank/kmp-app-template) ・ [ios-app-template](https://github.com/yossibank/ios-app-template)

## 構成

```mermaid
flowchart LR
    SHARED["shared<br/><i>共通コア</i>"]
    CORE[":core<br/><i>画面をまたぐ仕組み</i>"]
    VM["PokemonListViewModel"]
    SCREEN["PokemonListScreen"]
    SHARED --> VM
    CORE --> VM
    VM -->|"StateFlow&lt;PokemonListUiState&gt;"| SCREEN
```

| モジュール | 役割 |
| --- | --- |
| `:app` | 画面 |
| `:core` | 画面をまたいで使う仕組み。純 JVM のモジュールで、共通コアにも Android にも依存しない |

## 動かし方

> [!NOTE]
> 共通コアを GitHub Packages から取得するため、`~/.gradle/gradle.properties` に `gpr.user` / `gpr.token` が必要です。

1. Android Studio で開くか、`make build` でビルドする
2. 変更したら `make verify` を通す
