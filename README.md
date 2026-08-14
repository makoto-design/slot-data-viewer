# slot-data-viewer

日々の実績データを見るための静的ビューアと、それを表示する Android アプリ。

- ビューア: <https://makoto-design.github.io/slot-data-viewer/>（閲覧にはパスワードが必要）

## 構成

```
docs/      公開ページ（ビューア本体 + データ）
android/   ページを表示する WebView アプリ
```

## データ

`docs/data/` は集計済みのデータ。月ごとに分かれていて、ビューアは表示する期間に
必要な月だけを読み込む。

```
docs/data/meta.json          鍵の作り方と照合用の値（ここだけ平文）
docs/data/index.bin          一覧
docs/data/<id>/index.bin     日別サマリー・項目一覧・月一覧
docs/data/<id>/2026-08.bin   その月の明細
```

`meta.json` 以外は AES-256-GCM で暗号化してある。鍵はパスワードから PBKDF2-SHA256
（20万回）で作る。ビューアはブラウザの WebCrypto で復号するので、パスワードを
知らなければ中身は読めない。ファイルの先頭 12 バイトが nonce。

復号後の中身は月ごとに次の形。容量を抑えるため、日付と項目名を配列のインデックスで
参照する。

```json
{
  "month": "2026-08",
  "dates":  ["2026-08-01", "2026-08-02"],
  "models": ["A", "B"],
  "rows": [[0, 1, 481, -1200, 6706, 97.5]]
}
```

`rows` の並びは `[日付index, 項目index, 番号, 差分, 回数, 率]`。

## ローカルで開く

```bash
python -m http.server 8778 --directory docs
```

WebCrypto を使うので `https` か `localhost` から開くこと。

## Android アプリ

```bash
cd android
./gradlew assembleDebug
```

`app/build/outputs/apk/debug/app-debug.apk` が出る。ページを WebView で開くだけで、
データは端末に持たない。パスワードは初回だけ入力すれば端末に記憶される。
画面を下に引っぱると再読み込みする。
