import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Random;

/**
 * 地獄のような設定項目を持つモーダルダイアログ。
 */
public class SettingsDialog extends JDialog {

    private JTree settingsTree;

    public SettingsDialog(Frame owner) {
        super(owner, "地獄の設定", true);

        // ツリーのルートノードを作成
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("全ての設定（変更は推奨されません）");

        // 地獄のノード群を生成してルートに追加
        createHellishNodes(root);

        // ツリーモデルからJTreeを生成
        settingsTree = new JTree(root);

        // JTreeの外観と動作をカスタマイズ
        settingsTree.setCellRenderer(new HellishTreeCellRenderer());
        settingsTree.addTreeSelectionListener(new HellishTreeSelectionListener());

        // JTreeをスクロール可能にする
        JScrollPane scrollPane = new JScrollPane(settingsTree);

        // ダイアログのコンテンツとしてスクロールパネルを追加
        getContentPane().add(scrollPane);

        // ダイアログのサイズと表示設定
        setSize(400, 600);
        setLocationRelativeTo(owner);

        // 6. 設定するほど状況が悪化する - 閉じるとブラウザが再起動
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // 親ブラウザを道連れに終了し、再起動を促す
                System.exit(0);
            }
        });
    }

    /**
     * 地獄のような設定項目ノードを生成し、親ノードに追加する。
     * @param root 親となるノード
     */
    private void createHellishNodes(DefaultMutableTreeNode root) {
        Random random = new Random();
        String[] categories = {"一般", "表示", "プライバシー", "ネットワーク", "パフォーマンス", "セキュリティ", "言語", "開発者ツール", "実験的機能", "その他"};

        for (int i = 0; i < 10; i++) {
            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(new SettingItem(categories[i], false));
            root.add(categoryNode);
            for (int j = 0; j < 20; j++) {
                // 約10%の確率で「押せる」設定項目を生成
                boolean isEnabled = random.nextInt(10) == 0;
                String itemName = "設定項目 " + (i * 20 + j + 1);
                DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new SettingItem(itemName, isEnabled));
                categoryNode.add(itemNode);
            }
        }
    }

    /**
     * 設定項目の情報を保持する内部クラス。
     */
    private static class SettingItem {
        String name;
        boolean isEnabled;

        public SettingItem(String name, boolean isEnabled) {
            this.name = name;
            this.isEnabled = isEnabled;
        }

        @Override
        public String toString() {
            // JTreeでの表示名
            return name;
        }
    }

    /**
     * JTreeのノードをどのように描画するかを定義するレンダラー。
     */
    private class HellishTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            
            // SettingItemオブジェクトの場合のみ処理
            if (userObject instanceof SettingItem) {
                SettingItem item = (SettingItem) userObject;
                if (leaf) { // 葉ノード（個別の設定項目）の場合
                    if (item.isEnabled) {
                        this.setEnabled(true);
                        this.setForeground(Color.BLACK);
                        this.setText(item.name + " (押せそう…？)");
                    } else {
                        this.setEnabled(false);
                    }
                } else { // カテゴリノードの場合
                     this.setEnabled(false); // カテゴリ自体は選択不可
                }
            }
            return this;
        }
    }

    /**
     * JTreeのノード選択イベントを処理するリスナー。
     */
    private class HellishTreeSelectionListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) settingsTree.getLastSelectedPathComponent();

            if (node == null || !node.isLeaf()) {
                return;
            }

            SettingItem item = (SettingItem) node.getUserObject();

                            // 「押せる」項目が選択されたら、エラーダイアログを表示し、クラッシュ
                        if (item.isEnabled) {
                            String[] messages = {
                                "エラー: 正常に失敗しました。",
                                "警告: 設定を変更する権限がありません（今後もありません）。",
                                "情報: この設定はダミーです。",
                                "致命的エラー: 0xCAFEDEAD - 予期せぬ成功。",
                                "処理は完了しましたが、何も変わりませんでした。"
                            };
                            Random random = new Random();
                            String message = messages[random.nextInt(messages.length)];
                            
                            JOptionPane.showMessageDialog(
                                    SettingsDialog.this,
                                    message,
                                    "致命的な設定エラー",
                                    JOptionPane.ERROR_MESSAGE);
                            
                            // ユーザーがダイアログを閉じる間もなくクラッシュさせる
                            new Thread(() -> {
                                try {
                                    Thread.sleep(700); // 絶妙な間
                                } catch (InterruptedException interruptedException) {
                                    // 無視
                                }
                                System.exit(0xDEADBEEF); // 洒落た終了コードで
                            }).start();
                        }        }
    }
}
