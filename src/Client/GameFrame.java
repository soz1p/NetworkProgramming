package Client;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class GameFrame extends JPanel {

    private Image backgroundImage;

    public GameFrame() {
        setLayout(null);
        setBackground(Color.white);

        // 배경 이미지 로드
        ImageIcon icon = new ImageIcon("/Users/sozip/NetworkProgramming/src/images/background.png");
        backgroundImage = icon.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 이미지를 패널 크기에 맞게 그립니다.
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    // ... (기타 UI 요소 및 로직 추가)
}
