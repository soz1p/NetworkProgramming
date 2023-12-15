package Client;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameFrame extends JPanel {

    private Image backgroundImage;
    private JLabel dealerLabel;
    private JLabel meLabel;
    private JLabel othersLabel;
    private BufferedImage cardImage;
    private Image backOfACard;
    private String Username;
    

    public GameFrame() {
        setLayout(null);
        setBackground(Color.white);

        // 배경 이미지 로드
        ImageIcon icon = new ImageIcon("src/images/background.png");
        backgroundImage = icon.getImage();

        // JTextField 초기화 및 위치 설정
        dealerLabel = new JLabel("Dealer : ");
        dealerLabel.setBounds(200, 20, 100, 30);
        dealerLabel.setForeground(Color.WHITE); // 텍스트 색상을 흰색으로 설정
        dealerLabel.setFont(dealerLabel.getFont().deriveFont(Font.BOLD)); // 텍스트를 굵게 설정
        add(dealerLabel);

        meLabel = new JLabel("Player :");
        meLabel.setBounds(50, 220, 100, 30);
        meLabel.setForeground(Color.WHITE); // 텍스트 색상을 흰색으로 설정
        meLabel.setFont(meLabel.getFont().deriveFont(Font.BOLD)); // 텍스트를 굵게 설정
        add(meLabel);

        othersLabel = new JLabel("Others Playing ... ");
        othersLabel.setBounds(450, 180, 150, 20);
        othersLabel.setForeground(Color.WHITE); // 텍스트 색상을 흰색으로 설정
        add(othersLabel);
       
        ImageIcon backOfACardIcon = new ImageIcon("src/images/backsideOfACard.jpg");
        backOfACard = backOfACardIcon.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 이미지를 패널 크기에 맞게 그립니다.
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        
        //player(me)
        g.drawImage(backOfACard, 50, 250, null);
        g.drawImage(backOfACard, 150, 250, null);
        
        //player2(others)
        g.drawImage(backOfACard, 450, 200, 35 ,50, null);
        g.drawImage(backOfACard, 500, 200, 35 ,50, null);
        
        //dealer
        g.drawImage(backOfACard, 200, 50, null);
        g.drawImage(backOfACard, 300, 50, null);

        if (cardImage != null) {
            int xPosition = 50; // 카드의 x 좌표
            int yPosition = 250; // 카드의 y 좌표

            g.drawImage(cardImage, xPosition, yPosition, null);
        }
    }
    

    // ... (기타 UI 요소 및 로직 추가)
}
