# slot-data-viewer

日々の実績データを見るための静的ビューアと、それを表示する Android アプリ。

- ビューア: <https://makoto-design.github.io/slot-data-viewer/>

## 構成

```
docs/      公開ページ（ビューア本体 + データ）
android/   ページを表示する WebView アプリ
```

`docs/data/` は集計済みの JSON。月ごとに分かれていて、ビューアは表示する期間に
必要な月だけを読み込む。

```
docs/data/index.json              一覧
docs/data/<id>/index.json         日別サマリー・項目一覧・月一覧
docs/data/<id>/2026-08.json       その月の明細
```

明細は容量を抑えるため、日付と項目名を配列のインデックスで参照する形にしてある。

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

## Android アプリ

```bash
cd android
./gradlew assembleDebug
```

`app/build/outputs/apk/debug/app-debug.apk` が出る。ページを WebView で開くだけで、
データは端末に持たない。一度読んだぶんはキャッシュに残る。画面を下に引っぱると再読み込みする。
