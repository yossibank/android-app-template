# 開発の手順

> [!NOTE]
> 初めて動かす順のコマンドを書く。
> 共通コアを取得するトークンと AWS へのログインも書く。
> 実行環境の版は [README](../README.md#-実行環境) にある。

## 🚀 動かし方

最初に、次の順にコマンドを実行する。

| 順 | コマンド | 内容 |
| --- | --- | --- |
| 1 | `make token` | 共通コアを取得するトークンを `~/.gradle/gradle.properties` に書き込む。12 時間有効で、Android Studio も使う |
| 2 | `make build` | ビルドする。Android Studio で開いてもよい |
| 3 | `make verify` | 変更したら通す |

## 🔧 共通コア

> [!IMPORTANT]
> 共通コアは AWS CodeArtifact から取得する。`make token` の前に、それを読めるプロファイルで AWS にログインする。
>
> SSO なら `aws sso login --profile <名前>` を使う。プロファイルは `AWS_PROFILE=<名前> make token` で渡す。

手元のものへの差し替えと、テンプレートから作ったときの置き換えは、README の[入れ方](../README.md#-入れ方)にある。
