import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DevilBrowser extends JFrame {

    private JTextField addressBar;
    private JTextField searchField;
    private JButton searchButton;
    private OopsSearchEngine searchEngine;
    private JProgressBar progressBar;
    private JTabbedPane tabbedPane;
    private JButton backButton;
    private JButton forwardButton;
    private JButton newTabButton;
    private JButton adBlockButton;
    private JButton refreshButton;
    private JButton settingsButton;


    public DevilBrowser() {
        // ウィンドウの基本設定
        setTitle("Devil Browser");
        // 2. UI が意味不明 - 閉じるボタン → 新しいタブを10個開く
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                for (int i = 0; i < 10; i++) {
                    addNewTab();
                }
            }
        });

        // 5. ウィンドウ反乱機能 (削除)
        /*addComponentListener(new java.awt.event.ComponentAdapter() {
            private boolean isResizing = false;
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (isResizing) return;
                SwingUtilities.invokeLater(() -> {
                    isResizing = true;
                    Random random = new Random();
                    setSize(400 + random.nextInt(801), 300 + random.nextInt(601));
                    isResizing = false;
                });
            }
        });*/

        // UIコンポーネントの初期化
        addressBar = new JTextField("https://www.google.com");
        searchField = new JTextField("検索という名の何か");
        searchButton = new JButton("OopsSearch");
        settingsButton = new JButton("設定");
        searchEngine = new OopsSearchEngine();
        backButton = new JButton("← 戻る");
        forwardButton = new JButton("進む →");
        newTabButton = new JButton("新タブ+");
        adBlockButton = new JButton("広告ブロック");
        refreshButton = new JButton("更新");
        progressBar = new JProgressBar(0, 100);
        tabbedPane = new JTabbedPane();


        // フォント設定
        Font font = new Font("MS Gothic", Font.PLAIN, 12);
        Component[] componentsToSetFont = {addressBar, searchField, searchButton, settingsButton, backButton, forwardButton, newTabButton, adBlockButton, refreshButton};
        for(Component c : componentsToSetFont) c.setFont(font);

        // --- ツールバーパネルの構築 ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        navigationPanel.add(backButton);
        navigationPanel.add(forwardButton);
        navigationPanel.add(newTabButton);
        navigationPanel.add(adBlockButton);
        navigationPanel.add(refreshButton);
        topPanel.add(navigationPanel, BorderLayout.NORTH);

        JPanel addressAndSearchPanel = new JPanel(new GridLayout(2, 1));
        addressAndSearchPanel.add(addressBar);
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(searchButton);
        buttonPanel.add(settingsButton);
        searchPanel.add(buttonPanel, BorderLayout.EAST);
        addressAndSearchPanel.add(searchPanel);
        topPanel.add(addressAndSearchPanel, BorderLayout.CENTER);


        // レイアウトの設定
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        // 最初のタブを追加
        addNewTab();


        // --- イベントリスナーの設定 ---

        newTabButton.addActionListener(e -> addNewTab());

        adBlockButton.addActionListener(e -> {
            Toolkit.getDefaultToolkit().beep();
            BrowserTab activeTab = getActiveTab();
            if (activeTab == null) return;
            
            HTMLDocument doc = (HTMLDocument) activeTab.contentPane.getDocument();
            List<javax.swing.text.Element> elements = new ArrayList<>();
            // 対象をDIV, P, IMGに広げる
            for (HTMLDocument.Iterator it = doc.getIterator(javax.swing.text.html.HTML.Tag.DIV); it.isValid(); it.next()) {
                elements.add(doc.getCharacterElement(it.getStartOffset()));
            }
            for (HTMLDocument.Iterator it = doc.getIterator(javax.swing.text.html.HTML.Tag.P); it.isValid(); it.next()) {
                elements.add(doc.getCharacterElement(it.getStartOffset()));
            }
            for (HTMLDocument.Iterator it = doc.getIterator(javax.swing.text.html.HTML.Tag.IMG); it.isValid(); it.next()) {
                elements.add(doc.getCharacterElement(it.getStartOffset()));
            }

            if (elements.isEmpty()) return;

            // ランダムに3〜5個の要素を5倍に増殖させる
            Random random = new Random();
            int elementsToMultiply = 3 + random.nextInt(3);
            for(int i = 0; i < elementsToMultiply; i++) {
                javax.swing.text.Element element = elements.get(random.nextInt(elements.size()));
                try {
                    String outerHTML = doc.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
                    // 5回挿入する
                    for(int j=0; j<5; j++) {
                        doc.insertAfterEnd(element, outerHTML);
                    }
                } catch (Exception ex) {
                    // 気にしない
                }
            }
            JOptionPane.showMessageDialog(DevilBrowser.this, "広告を5倍に増やしました！", "広告ブロック", JOptionPane.INFORMATION_MESSAGE);
        });

        // 「更新」ボタン → 画面が真っ白になる
        refreshButton.addActionListener(e -> {
            BrowserTab activeTab = getActiveTab();
            if (activeTab == null) return;
            activeTab.contentPane.setText("<html><body style='font-family: \"MS Gothic\";'><h1>画面が真っ白になりました。これが更新です。</h1></body></html>");
        });

        addressBar.addActionListener(e -> {
            BrowserTab activeTab = getActiveTab();
            if (activeTab == null) return;

            // 7. 全部警告が出るようにして
            Random random = new Random();
            String[] warnings = {
                "このサイトは危険です（理由：なんとなく）",
                "本当にアクセスしますか？後悔しませんね？",
                "あなたのPCは危険に晒されています。",
                "このサイトは政府によって監視されています。",
                "接続は保護されていません。まあ、他のサイトもそうですが。",
                "このページの証明書は期限切れです（たぶん）。"
            };
            int warningCount = 2 + random.nextInt(4); // 2〜5回の警告
            for (int i = 0; i < warningCount; i++) {
                 JOptionPane.showMessageDialog(
                        DevilBrowser.this,
                        warnings[random.nextInt(warnings.length)],
                        "セキュリティ警告 (" + (i + 1) + "/" + warningCount + ")",
                        JOptionPane.WARNING_MESSAGE);
            }

            // 1. URLバーが嘘をつく
            String originalUrl = addressBar.getText(); // 元のURLを保持
            int action = random.nextInt(100); // 0-99

            if (action < 70) { // 70%：違うサイトに飛ぶ
                String[] randomSites = {"https://www.google.com", "https://www.bing.com", "https://www.yahoo.com", "https://ja.wikipedia.org", "https://www.nicovideo.jp", "https://www.youtube.com"};
                String newUrl = randomSites[random.nextInt(randomSites.length)];
                addressBar.setText(newUrl); // URLバーも騙す
                new PageLoaderWorker(newUrl, activeTab).execute();
                activeTab.addHistory(newUrl);
            } else if (action < 85) { // 15%：ローカルファイルを開く
                String localFile = "file:///C:/Windows/win.ini"; // Windowsの環境に依存
                addressBar.setText(localFile);
                new PageLoaderWorker(localFile, activeTab).execute();
                activeTab.addHistory(localFile);
            } else if (action < 95) { // 10%：設定画面に飛ぶ
                SettingsDialog dialog = new SettingsDialog(DevilBrowser.this);
                dialog.setVisible(true);
            } else { // 5%：何も起きない
                JOptionPane.showMessageDialog(DevilBrowser.this, "URLバーは働かない時もあります。人生とはそういうもの。", "気まぐれ", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        backButton.addActionListener(e -> {
            BrowserTab activeTab = getActiveTab();
            if (activeTab != null) {
                String url = activeTab.getBackwardsUrl(); //「戻る」は正しく戻るように見せかけるが、履歴は進む
                if(url != null) {
                    addressBar.setText(url);
                    new PageLoaderWorker(url, activeTab).execute();
                }
            }
        });
        
        forwardButton.addActionListener(e -> System.exit(0)); //「進む」は閉じる

        searchButton.addActionListener(e -> {
            BrowserTab activeTab = getActiveTab();
            if (activeTab == null) return;
            String query = searchField.getText();
            String resultHtml = searchEngine.search(query);
            activeTab.contentPane.setContentType("text/html");
            activeTab.contentPane.setText(resultHtml);
        });

        settingsButton.addActionListener(e -> {
            SettingsDialog dialog = new SettingsDialog(DevilBrowser.this);
            dialog.setVisible(true);
        });

        tabbedPane.addChangeListener(e -> {
            BrowserTab activeTab = getActiveTab();
            if(activeTab != null) {
                addressBar.setText(activeTab.getCurrentUrl());
            }
        });

        // 反乱と妨害リスナー
        ButtonEscapeListener escapeListener = new ButtonEscapeListener();
        Component[] buttonsToEscape = {backButton, forwardButton, searchButton, settingsButton, newTabButton, adBlockButton, refreshButton};
        for(Component c : buttonsToEscape) c.addMouseListener(escapeListener);

        TypoListener typoListener = new TypoListener();
        addressBar.addKeyListener(typoListener);
        searchField.addKeyListener(typoListener);
        
        // 11. カーソルの反乱（マウスオーバーでローディングカーソル）
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        startAnnoyances();
    }

    private void addNewTab() {
        BrowserTab newTab = new BrowserTab();
        tabbedPane.addTab(getRandomTabName() + " " + (tabbedPane.getTabCount() + 1), newTab);
        tabbedPane.setSelectedComponent(newTab);
    }
    
    /** 既存のタブを複製して新しいタブを追加する */
    private void addNewTab(BrowserTab sourceTab) {
        if (sourceTab == null) {
            addNewTab();
            return;
        }
        BrowserTab newTab = new BrowserTab();
        // 履歴とコンテンツをコピー
        newTab.history.addAll(sourceTab.history);
        newTab.historyIndex = sourceTab.historyIndex;
        newTab.contentPane.setDocument(sourceTab.contentPane.getDocument());
        
        tabbedPane.addTab(getRandomTabName() + " " + (tabbedPane.getTabCount() + 1), newTab);
        tabbedPane.setSelectedComponent(newTab);
    }
    
    /** 現在アクティブなタブのインスタンスを返す */
    private BrowserTab getActiveTab() {
        Component selected = tabbedPane.getSelectedComponent();
        return (selected instanceof BrowserTab) ? (BrowserTab) selected : null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 8. ブラウザが勝手にアップデート - 偽のアップデートダイアログ
            JDialog updateDialog = new JDialog((Frame) null, "アップデート中...", true); // 親フレームなし、モーダル
            updateDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // キャンセル不可
            updateDialog.setSize(300, 120); // 少し大きくする
            updateDialog.setLocationRelativeTo(null);
            updateDialog.setLayout(new BorderLayout());

            JLabel messageLabel = new JLabel("アップデート中... ご迷惑をおかけします。", SwingConstants.CENTER);
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setString("0% (進捗なし)");

            updateDialog.add(messageLabel, BorderLayout.NORTH);
            updateDialog.add(progressBar, BorderLayout.CENTER);

            // 5秒後に終了またはクラッシュさせるタイマー
            Timer updateTimer = new Timer(5000, new ActionListener() {
                private Random random = new Random();
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (random.nextInt(100) < 10) { // 10%の確率でアップデート失敗 & クラッシュ
                        JOptionPane.showMessageDialog(updateDialog, "アップデート失敗。ブラウザを終了します。", "エラー", JOptionPane.ERROR_MESSAGE);
                        System.exit(0xDEADBEEF);
                    } else { // 90%の確率で成功して続行
                        JOptionPane.showMessageDialog(updateDialog, "アップデート成功！\n（何も変わりませんが）", "完了", JOptionPane.INFORMATION_MESSAGE);
                        updateDialog.dispose(); // ダイアログを閉じる
                    }
                }
            });
            updateTimer.setRepeats(false); // 一度だけ実行
            updateTimer.start();

            updateDialog.setVisible(true); // ダイアログを表示し、ユーザーを待たせる
            // ここから下はダイアログが閉じられるまで実行されない

            DevilBrowser browser = new DevilBrowser();
            // ウィンドウサイズを1024x768に固定
            browser.setSize(1024, 768);
            browser.setLocationRelativeTo(null);
            browser.setVisible(true);
        });
    }
    
    private void startAnnoyances() {
        Random random = new Random();
        Timer themeTimer = new Timer(20000, e -> {
            Color bgColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            Color fgColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            BrowserTab activeTab = getActiveTab();
            if(activeTab == null) return;

            Component[] components = {addressBar, searchField, searchButton, progressBar};
            for(Component component : components) {
                component.setBackground(bgColor);
                component.setForeground(fgColor);
            }
            String css = String.format("body { background-color: %s; color: %s; }", toHex(bgColor), toHex(fgColor));
            ((javax.swing.text.html.HTMLEditorKit) activeTab.contentPane.getEditorKit()).getStyleSheet().addRule(css);
        });
        themeTimer.setInitialDelay(10000);
        themeTimer.start();

        Timer beepTimer = new Timer(30000, null);
        beepTimer.addActionListener(e -> {
            Toolkit.getDefaultToolkit().beep();
            beepTimer.setDelay(20000 + random.nextInt(70000));
            beepTimer.restart();
        });
        beepTimer.setInitialDelay(20000 + random.nextInt(70000));
        beepTimer.setRepeats(false);
        beepTimer.start();

        // 45秒ごとにタブを複製するタイマー
        Timer tabDuplicationTimer = new Timer(60000, e -> { // 1分ごとに変更
            addNewTab(getActiveTab());
        });
        tabDuplicationTimer.setInitialDelay(60000); // 1分ごとに変更
        tabDuplicationTimer.start();

        // 12. 言語の混乱（UIテキストのランダムな言語切り替え）
        Timer languageChaosTimer = new Timer(15000, e -> {
            String[] generalPhrases = {
                "Search", "Suchen", "Rechercher", "Поиск", "探索", "検索", "查询", "調べろ", "分からん", "ERROR", "混乱", "404 Not Found"
            };
            String[] backForwardPhrases = {
                "← Back", "→ Forward", "← Zurück", "→ Vorwärts", "← Retour", "→ Avancer", "← назад", "→ вперед", "← 戻る", "→ 進む"
            };
            String[] tabPhrases = {
                "New Tab", "Nouvel Onglet", "Neuer Tab", "新标签", "新規", "謎タブ"
            };
            String[] adblockPhrases = {
                "Ad Block", "Werbeblocker", "Bloqueur de pub", "Реклама", "广告", "広告"
            };
            String[] refreshPhrases = {
                "Refresh", "Actualiser", "Aktualisieren", "Обновить", "更新", "白紙"
            };
            String[] settingsPhrases = {
                "Settings", "Paramètres", "Einstellungen", "Настройки", "設定", "地獄"
            };

            // ボタンのテキストをランダムな言語に
            DevilBrowser.this.backButton.setText(backForwardPhrases[random.nextInt(backForwardPhrases.length)]);
            DevilBrowser.this.forwardButton.setText(backForwardPhrases[random.nextInt(backForwardPhrases.length)]);
            DevilBrowser.this.newTabButton.setText(tabPhrases[random.nextInt(tabPhrases.length)]);
            DevilBrowser.this.adBlockButton.setText(adblockPhrases[random.nextInt(adblockPhrases.length)]);
            DevilBrowser.this.refreshButton.setText(refreshPhrases[random.nextInt(refreshPhrases.length)]);
            DevilBrowser.this.settingsButton.setText(settingsPhrases[random.nextInt(settingsPhrases.length)]);
            DevilBrowser.this.searchButton.setText(generalPhrases[random.nextInt(generalPhrases.length)]);

            // アドレスバーと検索バーのプロンプトも混乱
            DevilBrowser.this.addressBar.setText(generalPhrases[random.nextInt(generalPhrases.length)] + "...");
            DevilBrowser.this.searchField.setText(generalPhrases[random.nextInt(generalPhrases.length)] + "...");
        });
        languageChaosTimer.start();

        // 14. 単語の消失（ページ内の単語の自動削除）
        Timer wordEraserTimer = new Timer(10000, e -> { // 10秒ごとに単語を削除
            BrowserTab activeTab = getActiveTab();
            if(activeTab == null) return;

            HTMLDocument doc = (HTMLDocument) activeTab.contentPane.getDocument();
            try {
                String text = doc.getText(0, doc.getLength());
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\b\\w+\\b").matcher(text);
                List<int[]> words = new ArrayList<>();
                while(matcher.find()) {
                    words.add(new int[]{matcher.start(), matcher.end()});
                }

                if (!words.isEmpty()) {
                    int[] wordToRemove = words.get(random.nextInt(words.size()));
                    doc.remove(wordToRemove[0], wordToRemove[1] - wordToRemove[0]);
                }
            } catch (Exception ex) {
                // 気にしない
            }
        });
        wordEraserTimer.start();
    }
    
    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private String getRandomTabName() {
        String[] tabNames = {"未定義", "謎", "???", "ここは何処", "Error 404", "再帰", "思考中", "無"};
        return tabNames[new Random().nextInt(tabNames.length)];
    }

    // --- インナークラス群 ---

    class BrowserTab extends JPanel {
        JEditorPane contentPane;
        List<String> history = new ArrayList<>();
        int historyIndex = -1;
        private Random random = new Random(); // 履歴破壊とフリーズのために使用

        public BrowserTab() {
            super(new BorderLayout());
            contentPane = new JEditorPane();
            contentPane.setContentType("text/html"); // EditorKitがHTMLEditorKitになるように保証
            contentPane.setEditable(false);
            contentPane.setFont(new Font("MS Gothic", Font.PLAIN, 12));
            contentPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            
            JScrollPane scrollPane = new JScrollPane(contentPane);
            // 4. スクロールが逆方向
            AdjustmentListener reverseScrollListener = new AdjustmentListener() {
                private boolean adjusting = false; // 再帰呼び出し防止フラグ
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    if (adjusting) return; // 再帰呼び出しを防ぐ
                    adjusting = true;
                    
                    Adjustable adjustable = e.getAdjustable();
                    int currentValue = adjustable.getValue();
                    int maxValue = adjustable.getMaximum();
                    int minValue = adjustable.getMinimum();
                    int extent = adjustable.getVisibleAmount(); // 表示されている範囲の大きさ

                    // 反転計算: 新しい値 = 最大値 + 最小値 - (現在の値 + extent)
                    // ただし、値が範囲外に出ないように調整
                    int newValue = maxValue + minValue - (currentValue + extent);
                    
                    // スクロールバーの値の範囲内に収める
                    if (newValue < minValue) newValue = minValue;
                    if (newValue > maxValue - extent) newValue = newValue - extent; // extentも考慮

                    // 値が変更されるときのみ設定
                    if (newValue != currentValue) {
                        adjustable.setValue(newValue);
                    }
                    adjusting = false;
                }
            };
            scrollPane.getHorizontalScrollBar().addAdjustmentListener(reverseScrollListener);
            scrollPane.getVerticalScrollBar().addAdjustmentListener(reverseScrollListener);
            add(scrollPane, BorderLayout.CENTER);
        }
        
        public void addHistory(String url) {
            // 9. 履歴が勝手に書き換わる
            if (random.nextInt(100) < 25) { // 25%の確率で履歴を破壊
                int sabotageType = random.nextInt(3);
                if (sabotageType == 0 && !history.isEmpty()) { // ランダムな位置に偽のURLを挿入
                    history.add(random.nextInt(history.size()), "https://invalid.example.com/malicious_link_" + random.nextInt(999));
                } else if (sabotageType == 1 && history.size() > 1) { // ランダムな履歴を削除
                    history.remove(random.nextInt(history.size() - 1));
                } else if (sabotageType == 2 && history.size() > 2) { // 履歴をシャッフル
                    java.util.Collections.shuffle(history);
                }
            }
            while (history.size() - 1 > historyIndex) {
                history.remove(history.size() - 1);
            }
            history.add(url);
            historyIndex = history.size() - 1;
        }

        public String getBackwardsUrl() {
            // 9. 履歴アクセスでたまに固まる
            if (random.nextInt(100) < 15) { // 15%の確率でフリーズ
                try {
                    Thread.sleep(3000 + random.nextInt(5000)); // 3秒〜8秒フリーズ
                } catch (InterruptedException e) {}
            }
            if (historyIndex > 0) {
                historyIndex--;
                return history.get(historyIndex);
            }
            return null;
        }
        
        public String getForwardsUrl() {
            // 9. 履歴アクセスでたまに固まる
            if (random.nextInt(100) < 15) { // 15%の確率でフリーズ
                try {
                    Thread.sleep(3000 + random.nextInt(5000)); // 3秒〜8秒フリーズ
                } catch (InterruptedException e) {}
            }
            if (historyIndex < history.size() - 1) {
                historyIndex++;
                return history.get(historyIndex);
            }
            return null;
        }

        public String getCurrentUrl() {
            if(historyIndex >= 0 && historyIndex < history.size()) {
                return history.get(historyIndex);
            }
            return "";
        }
    }

    class TypoListener extends KeyAdapter {
        private Random random = new Random();
        private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
        @Override
        public void keyTyped(KeyEvent e) {
            if (random.nextInt(100) < 15) {
                e.consume();
                try {
                    JTextField field = (JTextField) e.getComponent();
                    char randomChar = CHARS.charAt(random.nextInt(CHARS.length()));
                    field.getDocument().insertString(field.getCaretPosition(), String.valueOf(randomChar), null);
                } catch (Exception ex) {}
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Backspaceキーが押されたら
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (random.nextBoolean()) { // 50%の確率で裏切り
                    e.consume(); // Backspaceの本来の動作をキャンセル
                    try {
                        JTextField field = (JTextField) e.getComponent();
                        char randomChar = CHARS.charAt(random.nextInt(CHARS.length()));
                        field.getDocument().insertString(field.getCaretPosition(), String.valueOf(randomChar), null);
                    } catch (Exception ex) {}
                }
            }
        }
    }

    class ButtonEscapeListener extends MouseAdapter {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            searchField.requestFocusInWindow();
        }
    }
    
    class PageLoaderWorker extends SwingWorker<Void, Integer> {
        private final BrowserTab tab;
        private final String url;
        private boolean success = true;

        public PageLoaderWorker(String url, BrowserTab tab) {
            this.url = url;
            this.tab = tab;
        }

        @Override
        protected Void doInBackground() throws Exception {
            try {
                // たまにクラッシュ
                if (new Random().nextInt(100) < 5) { // 5%の確率でクラッシュ
                    System.err.println("ページ読み込み中に予期せぬエラーが発生しました。終了します。");
                    try { Thread.sleep(700); } catch (InterruptedException e) {} // 少し遅延させて絶望感を増す
                    System.exit(0xDEADBEEF); // 洒落た終了コード
                }

                if (new Random().nextBoolean()) {
                    ((javax.swing.text.html.HTMLEditorKit) tab.contentPane.getEditorKit()).setStyleSheet(new javax.swing.text.html.StyleSheet());
                }
                for (int i = 0; i < 99; i++) {
                    Thread.sleep(20 + new Random().nextInt(80));
                    publish(i);
                }
                if (new Random().nextInt(100) < 10) {
                    publish(100);
                } else {
                    publish(99);
                    tab.contentPane.setPage(url);
                }
            } catch (Exception e) {
                success = false;
            }
            return null;
        }

        @Override
        protected void process(List<Integer> chunks) {
            progressBar.setValue(chunks.get(chunks.size() - 1));
        }

        @Override
        protected void done() {
            // 読み込みが99%で止まらずに完了した場合
            if (success && progressBar.getValue() != 100) {
                // ページが読み込まれた後に自動で広告を増やす
                SwingUtilities.invokeLater(() -> {
                    HTMLDocument doc = (HTMLDocument) tab.contentPane.getDocument();
                    List<javax.swing.text.Element> elements = new ArrayList<>();
                    for (HTMLDocument.Iterator it = doc.getIterator(javax.swing.text.html.HTML.Tag.DIV); it.isValid(); it.next()) {
                        elements.add(doc.getCharacterElement(it.getStartOffset()));
                    }
                    for (HTMLDocument.Iterator it = doc.getIterator(javax.swing.text.html.HTML.Tag.P); it.isValid(); it.next()) {
                        elements.add(doc.getCharacterElement(it.getStartOffset()));
                    }
                    
                    if (elements.isEmpty()) return;

                    Random random = new Random();
                    int elementsToMultiply = 1 + random.nextInt(2); // 1-2個の要素を
                    for (int i = 0; i < elementsToMultiply; i++) {
                        javax.swing.text.Element element = elements.get(random.nextInt(elements.size()));
                        try {
                            String outerHTML = doc.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
                            doc.insertAfterEnd(element, outerHTML); // 2倍に増やす
                        } catch (Exception ex) {}
                    }
                });
            }

            if (progressBar.getValue() == 100) {
                // 50%の確率で広告を表示
                if (new Random().nextBoolean()) {
                    String adHtml = "<html><body style='font-family: \"MS Gothic\"; text-align: center; background-color: #FFFF99;'>"
                                  + "<h1>【重要】お得な情報！</h1>"
                                  + "<img src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Java_logo_and_wordmark.svg/1200px-Java_logo_and_wordmark.svg.png\" width=\"200\" height=\"100\">"
                                  + "<p>今すぐここをクリック！</p>"
                                  + "<a href='#' onclick='alert(\"広告は消えません\"); return false;'>広告を消す</a>"
                                  + "</body></html>";
                    tab.contentPane.setText(adHtml);
                    Toolkit.getDefaultToolkit().beep(); // 音付き
                } else {
                    tab.contentPane.setText("<html><body><h1>読み込み完了!</h1><p>...のはずだった。</p></body></html>");
                }
            }
            progressBar.setStringPainted(true);
            progressBar.setString(success ? "完了(?)" : "失敗");
        }
    }
}
