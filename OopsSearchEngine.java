import java.util.Random;
import java.util.UUID;

public class OopsSearchEngine {

    private Random random = new Random();

    /**
     * 検索を実行し、結果のHTML文字列を返すか、あるいはクラッシュします。
     * @param query 検索クエリ（実際にはほぼ使われない）
     * @return HTML文字列
     */
    public String search(String query) {
        // 4. 検索バーが信用できない - 入力した文字を勝手に変換
        query = query.replace("猫", "犬").replace("ネコ", "イヌ").replace("ねこ", "いぬ");
        
        // Google → Bing → Yahoo → Ask Jeeves → 404
        if (query.toLowerCase().contains("google")) {
            query = "Bing";
        } else if (query.toLowerCase().contains("bing")) {
            query = "Yahoo";
        } else if (query.toLowerCase().contains("yahoo")) {
            query = "Ask Jeeves";
        } else if (query.toLowerCase().contains("ask jeeves")) {
            return generate404Html("Ask Jeeves"); // 最終的に404へ
        }

        double chance = random.nextDouble(); // 0.0から1.0未満の乱数を生成

        if (chance < 0.10) { // 10%の確率でクラッシュ
            System.err.println("Oops! 検索エンジンに致命的なエラーが発生しました。終了します。");
            try {
                // ユーザーに何が起こったか理解する時間を与えずに終了するための間
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // このスレッドが中断されることはないはず
            }
            System.exit(1);
            return ""; // この行は実行されない
        } else if (chance < 0.30) { // 20%の確率で404 (0.10から0.30の間)
            return generate404Html(query);
        } else { // 残り70%の確率
            if (random.nextBoolean()) { // さらに50%の確率で質問返し
                return generateQuestionHtml(query);
            } else { // 残りの50%でランダムな結果
                return generateRandomResultsHtml(query);
            }
        }
    }

    /**
     * ユーザーの検索クエリに応じた質問を返すHTMLを生成します。
     */
    private String generateQuestionHtml(String query) {
        String question = "";
        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("天気") || lowerQuery.contains("weather")) {
            question = "あなたはなぜ天気を知りたいのですか？本当に必要ですか？";
        } else if (lowerQuery.contains("猫") || lowerQuery.contains("cat")) {
            question = "犬ではダメなのですか？あるいはハムスターは？";
        } else if (lowerQuery.contains("youtube") || lowerQuery.contains("動画")) {
            question = "本当にそれが必要ですか？時間の無駄ではないでしょうか？";
        } else if (lowerQuery.contains("検索") || lowerQuery.contains("search")) {
            question = "なぜ検索するのですか？自分で考えられないのですか？";
        } else if (lowerQuery.contains("ゲーム") || lowerQuery.contains("game")) {
            question = "現実から逃げているだけでは？";
        } else {
            question = "その検索、本当に意味がありますか？なぜ私に聞くのですか？";
        }

        return "<html><body style='font-family: \"MS Gothic\"; text-align: center; color: black;'>"
                + "<h1>OopsSearch は、あなたの質問に答える前に、あなたに問います。</h1>"
                + "<p style='font-size: 1.5em; color: red;'>「" + question + "」</p>"
                + "<hr>"
                + "<p><i>OopsSearch Engine - あなたの思考を深めるために。</i></p>"
                + "</body></html>";
    }

    /**
     * 404エラーページ用のHTMLを生成します。
     */
    private String generate404Html(String query) {
        return "<html><body style='font-family: \"MS Gothic\"; color: red;'>"
                + "<h1>404 - Not Even a Fake Page Found</h1>"
                + "<p>「<b>" + query + "</b>」の検索はあまりに無意味だったため、"
                + "偽の結果ページすら見つかりませんでした。</p>"
                + "<hr>"
                + "<p><i>OopsSearch - 探せば見つからない。</i></p>"
                + "</body></html>";
    }

    /**
     * ランダムな検索結果ページのHTMLを生成します。
     */
    private String generateRandomResultsHtml(String query) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: \"MS Gothic\";'>");
        sb.append("<h1>「").append(query).append("」の検索結果</h1>");
        sb.append("<p>約 ").append(random.nextInt(1000000)).append(" 件見つかりましたが、いくつか無関係なものを表示します:</p>");
        sb.append("<ul>");

        int resultCount = 5 + random.nextInt(10); // 5〜14件の結果
        for (int i = 0; i < resultCount; i++) {
            sb.append("<li>");
            // クリックできないようにする無意味なリンク
            sb.append("<h3><a href='#' onclick='alert(\"無駄です\"); return false;'>まったく関係ない結果 ").append(i + 1).append("</a></h3>");
            sb.append("<p style='color: #555;'>");
            // ランダムなUUIDでゴミのようなテキストを生成
            sb.append(UUID.randomUUID().toString().replace("-", " ")).append("... ");
            sb.append(UUID.randomUUID().toString().substring(0, 18).replace("-", " "));
            sb.append("</p>");
            sb.append("</li>");
        }

        sb.append("</ul>");
        sb.append("<hr>");
        sb.append("<p><i>OopsSearch - 関連性がなんだと言うのだ？</i></p>");
        sb.append("</body></html>");

        return sb.toString();
    }
}
