# 🤖 android-app-template

Jetpack Compose と Kotlin Multiplatform で作る、商品一覧アプリのテンプレート

[![Verify](https://img.shields.io/github/actions/workflow/status/yossibank/android-app-template/verify.yml?label=Verify&logo=github)](https://github.com/yossibank/android-app-template/actions/workflows/verify.yml)
[![License](https://img.shields.io/github/license/yossibank/android-app-template)](LICENSE)

![Android 7+](https://img.shields.io/badge/Android-7%2B-3DDC84?logo=android&logoColor=white)
![Kotlin 2.4](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Coroutines](https://img.shields.io/badge/Coroutines-7F52FF?logo=kotlin&logoColor=white)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin_Multiplatform-7F52FF?logo=kotlin&logoColor=white)

<table>
  <tr>
    <th>ライト</th>
    <th>ダーク</th>
  </tr>
  <tr>
    <td><img src="docs/images/list-light.png" width="260" alt="商品の一覧（ライトモード）"></td>
    <td><img src="docs/images/list-dark.png" width="260" alt="商品の一覧（ダークモード）"></td>
  </tr>
</table>

DummyJSON にログイン ・ 無限スクロール ・ 商品名で絞り込み

## 🗺️ 全体像

共通コアと 2 つのアプリを、3 つのリポジトリに分けている。

```mermaid
flowchart LR
    KMP["🧩 kmp-app-template<br/>共通ロジック"]
    AND["🤖 android-app-template<br/>Android アプリ"]
    IOS["🍎 ios-app-template<br/>iOS アプリ"]
    KMP -->|"AAR<br/>AWS CodeArtifact"| AND
    KMP -->|"Shared.xcframework<br/>GitHub Releases + SPM"| IOS
    style AND stroke-width:3px
```

[🧩 kmp-app-template](https://github.com/yossibank/kmp-app-template) ・ [🍎 ios-app-template](https://github.com/yossibank/ios-app-template)

### モジュール構成

アプリは、次のモジュールに分けている。

```mermaid
flowchart LR
    SHARED["🧩 shared<br/>共通コア"]
    SCREEN[":core:screen"]
    HOME[":feature:home"]
    LOGIN[":feature:login"]
    APP[":app"]
    SHARED --> HOME
    SCREEN --> HOME
    SHARED --> LOGIN
    SCREEN --> LOGIN
    HOME --> APP
    LOGIN --> APP
```

| モジュール | 役割 |
| --- | --- |
| `:core:screen` | 画面の土台（読み込み状態の管理）と、機能に依らない UI 部品 |
| `:feature:home` | 一覧の画面 |
| `:feature:login` | ログインの画面 |
| `:app` | 共通コアの設定と、ログイン状態に応じた画面の切り替え |

## 🚀 動かし方

最初に、次の順にコマンドを実行する。

| 順 | コマンド | 内容 |
| --- | --- | --- |
| 1 | `make token` | 共通コアを取得するトークンを `~/.gradle/gradle.properties` に書き込む。12 時間有効で、Android Studio も使う |
| 2 | `make build` | ビルドする。Android Studio で開いてもよい |
| 3 | `make verify` | 変更したら通す |

> [!IMPORTANT]
> 共通コアは AWS CodeArtifact から取得する。`make token` の前に、それを読めるプロファイルで AWS にログインする。
>
> SSO なら `aws sso login --profile <名前>` を使う。プロファイルは `AWS_PROFILE=<名前> make token` で渡す。

### 共通コアを手元のものに差し替える

kmp-app-template のディレクトリを絶対パスで渡す。このときはトークンは要らない。

```sh
SHARED_DIR=/path/to/kmp-app-template make verify
```

Android Studio では、`~/.gradle/gradle.properties` にパスを書く。

```properties
shared.dir=/path/to/kmp-app-template
```

### テンプレートから作ったとき

パッケージの接頭辞と GitHub のオーナーを置き換える。3 つのリポジトリそれぞれで実行する。

```sh
scripts/rename.sh <GitHub のオーナー> <パッケージの接頭辞>    # 例: scripts/rename.sh acme com.acme
```

