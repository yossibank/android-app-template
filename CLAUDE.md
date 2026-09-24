# android-app-template

- コメントを書かない。コード、設定、スクリプト、CI のいずれにも書かない。要ると判断したら、書かずに提案する。
- 変更したら `make verify` を通す。`verify` から `assembleRelease` を外さない（R8 の縮小の壊れに気づけなくなる）。
- 共通ロジックは kmp-app-template に置く。ここには Android 固有のものだけを置く。
- 同じ画面が ios-app-template にもある。挙動を変えるときは向こうに合わせる。
  絞り込みの一致には `standardContains` を使い、素の `contains` を使わない。
- 失敗画面と絞り込み 0 件の見せ方は各 OS の作法に寄せる。揃えるのは文言の語彙まで。
- androidTest は無効化している。書くなら `app/build.gradle.kts` の `enableAndroidTest` を戻す。
- バージョンを `gradle/libs.versions.toml` 以外で指定しない。
- `.gitignore` に `*.jar` を追加しない（`gradle-wrapper.jar` が消える）。
