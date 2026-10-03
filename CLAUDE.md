# android-app-template

## 🛠️ 作業の進め方

- 変更したら `make verify` を通す。
- androidTest は無効化している。書くなら `app/build.gradle.kts` の `enableAndroidTest` を戻す。
- バージョンを `gradle/libs.versions.toml` 以外で指定しない。
- `.gitignore` に `*.jar` を追加しない（`gradle-wrapper.jar` が消える）。

> [!WARNING]
> `verify` から `assembleRelease` を**外さない**（R8 の縮小の壊れに気づけなくなる）。

## ✍️ コードの書き方

> [!IMPORTANT]
> **コメントを書かない。** コード、設定、スクリプト、CI のいずれにも書かない。要ると判断したら、書かずに提案する。Renovate が Actions の SHA の横に付けるバージョンのコメントは除く。

## 🔗 3 リポジトリの取り決め

- 共通ロジックは kmp-app-template に置く。ここには Android 固有のものだけを置く。
- 同じ画面が ios-app-template にもある。挙動を変えるときは向こうに合わせる。
  - 絞り込みの一致には `standardContains` を使い、素の `contains` を使わない。
- 失敗画面と絞り込み 0 件は、次のように揃える。

| | 揃え方 |
| --- | --- |
| 見せ方 | 各 OS の作法に寄せる |
| 文言の語彙 | 揃える |
