package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class CardPanel extends JPanel {
	private int dealerCardX = 200;
	private int playerCardX = 50;
	private int dealerCardY = 50;
	private int playerCardY = 250;
	private String username;
	
	  private List<String[]> cards = new ArrayList<>(); // 카드의 정보를 저장하는 리스트


    public CardPanel(String username) {
        setOpaque(false); // 패널을 투명하게 만듭니다.
        setLayout(null);
        this.username = username;
        //setBackground(Color.yellow);
    }

    public void drawCard(String playerID, String suit, String rank) {
    	
    	String actualPlayername = playerID.split(":")[0].substring(1);
    	
    	if (!actualPlayername.equals(username) && !playerID.equals("dealer")) {
            return;
        }
    	if(playerID.equals("dealer")) {
    		actualPlayername = "dealer";
    	}
    	
        int xPosition;
        int yPosition;
        switch (actualPlayername) {
            case "dealer":
                xPosition = dealerCardX;
                yPosition = dealerCardY;
                dealerCardX += 100; // 다음 카드의 x 좌표를 조정합니다.
                if(dealerCardX > 500) {
                	dealerCardX = 200;
                }
                break;
            default:
                xPosition = playerCardX;
                yPosition = playerCardY;
                playerCardX += 100; // 다음 카드의 x 좌표를 조정합니다.
                if(dealerCardX > 500) {
                	dealerCardX = 50;
                }
                break;
                
        }

        // 카드 이미지 파일의 경로를 리스트에 추가합니다.
        cards.add(new String[] {"src/images/" + suit + "_" + rank + ".jpg", String.valueOf(xPosition), String.valueOf(yPosition)});

        // 패널을 다시 그리도록 repaint()를 호출합니다.
        repaint();
    }
    
    public void reset() {
        cards.clear(); // 카드 정보를 저장하는 리스트를 비웁니다.
        dealerCardX = 200; // 카드의 x 좌표를 초기화합니다.
        playerCardX = 50; // 카드의 x 좌표를 초기화합니다.
        repaint(); // 패널을 다시 그립니다.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
       
        // 카드 이미지를 그립니다.
        for (String[] card : cards) {
            System.out.println(Arrays.toString(card)); // 카드 정보 출력
            Image cardImage = new ImageIcon(card[0]).getImage();
            int xPosition = Integer.parseInt(card[1]); // 카드의 x 좌표
            int yPosition = Integer.parseInt(card[2]); // 카드의 x 좌표
            g.drawImage(cardImage, xPosition, yPosition,75, 100, null);
        }
    }
}