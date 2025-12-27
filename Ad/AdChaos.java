import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AdChaos {

    public static void main(String[] args) {
        // Swing 関連は EDT 上で開始
        SwingUtilities.invokeLater(() -> {
            startAdSpawner();       // 広告ウィンドウを増殖させる
            startNotificationLoop(); // 定期的に通知を出す
        });
    }

    // -------------------------------
    // 1. 一定間隔で広告ウィンドウを生成（増殖）
    // -------------------------------
    private static void startAdSpawner() {
        Timer spawnTimer = new Timer(true);

        spawnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // ウィンドウ生成は EDT で行う
                SwingUtilities.invokeLater(AdChaos::createAdWindow);
            }
        }, 0, 5000); // 5秒ごとに新しいウィンドウを生成
    }

    // 広告ウィンドウを1つ作成
    private static void createAdWindow() {
        JFrame frame = new JFrame();

        frame.setSize(350, 250);

        // 透明度を設定するためには、必ず「装飾なし」にする必要がある
        frame.setUndecorated(true);

        // 半透明（0.0f = 完全透明, 1.0f = 不透明）
        try {
            frame.setOpacity(0.85f);
        } catch (UnsupportedOperationException e) {
            System.out.println("Opacity is not supported on this platform.");
        }

        frame.setAlwaysOnTop(true);

        // 画像広告のラベル
        JLabel imageLabel = new JLabel();
        try {
            Image img;
            try {
                // まずは URL から画像を取得してみる（ネット接続があればこちらが使われる）
                img = new ImageIcon(new URL("https://via.placeholder.com/300x150.png")).getImage();
            } catch (Exception e) {
                // 失敗した場合は、ローカルファイル "ad.png" を使用
                img = new ImageIcon("ad.png").getImage();
            }
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imageLabel.setText("Image failed to load");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JLabel text = new JLabel("Special Offer! Click for details.", SwingConstants.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(text, BorderLayout.SOUTH);

        frame.setContentPane(panel);

        // 画面サイズを取得してランダムに配置
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Random rand = new Random();
        int x = rand.nextInt(Math.max(1, screen.width - frame.getWidth()));
        int y = rand.nextInt(Math.max(1, screen.height - frame.getHeight()));
        frame.setLocation(x, y);

        frame.setVisible(true);

        // ウィンドウを動かす
        startWindowMovement(frame);
    }

    // -------------------------------
    // 2. ウィンドウを画面内で動かす（バウンドする広告）
    // -------------------------------
    private static void startWindowMovement(JFrame frame) {
        Timer moveTimer = new Timer(true);
        Random rand = new Random();

        moveTimer.scheduleAtFixedRate(new TimerTask() {
            int dx = rand.nextInt(7) + 2;  // X 方向の移動量
            int dy = rand.nextInt(7) + 2;  // Y 方向の移動量

            @Override
            public void run() {
                // EDT 上で位置更新
                SwingUtilities.invokeLater(() -> {
                    if (!frame.isDisplayable()) {
                        // ウィンドウが閉じられていたらタイマーはそのまま放置（GC で回収される）
                        cancel();
                        return;
                    }

                    Point p = frame.getLocation();
                    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

                    int newX = p.x + dx;
                    int newY = p.y + dy;

                    // 画面端でバウンド
                    if (newX < 0 || newX + frame.getWidth() > screen.width) {
                        dx = -dx;
                        newX = p.x + dx;
                    }
                    if (newY < 0 || newY + frame.getHeight() > screen.height) {
                        dy = -dy;
                        newY = p.y + dy;
                    }

                    frame.setLocation(newX, newY);
                });
            }
        }, 0, 30); // 約30msごとに位置を更新（なめらかに動く）
    }

    // -------------------------------
    // 3. 定期的に通知を表示
    // -------------------------------
    private static void startNotificationLoop() {
        Timer notifyTimer = new Timer(true);

        notifyTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                showNotification("New Ad Available", "A new promotional window has appeared.");
            }
        }, 0, 10000); // 10秒ごとに通知
    }

    // 通知を1回表示
    private static void showNotification(String title, String message) {
        try {
            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray is not supported on this system.");
                return;
            }

            SystemTray tray = SystemTray.getSystemTray();
            Image img = Toolkit.getDefaultToolkit().createImage(new byte[0]);
            TrayIcon icon = new TrayIcon(img, "Ad Notification");
            icon.setImageAutoSize(true);

            tray.add(icon);
            icon.displayMessage(title, message, TrayIcon.MessageType.INFO);

            // 数秒後にアイコンを削除
            Timer removeTimer = new Timer(true);
            removeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tray.remove(icon);
                }
            }, 4000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
