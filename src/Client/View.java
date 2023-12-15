package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class View extends JFrame {
  private JPanel contentPane;
  private JTextField txtInput;
  private String UserName;
  private JButton btnSend;
  private JTextArea textArea;
  private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
  private Socket socket; // 연결소켓
  private InputStream is;
  private OutputStream os;
  private DataInputStream dis;
  private DataOutputStream dos;
  private JLabel lblUserName;
  private JButton btnHit;
  private JButton btnStay;
  private JButton btnExit;
  private JButton btnJoin;
  private JButton btnStart;
  private GameFrame gameFrame;
  private CardPanel cardPanel;
  private JLayeredPane layeredPane;

  /**
   * Create the frame.
   */
  public View(String username, String ip_addr, String port_no) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 392, 462);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(12, 10, 352, 400);
    contentPane.add(scrollPane);

    textArea = new JTextArea();
    textArea.setEditable(false);
    scrollPane.setViewportView(textArea);

    txtInput = new JTextField();
    txtInput.setBounds(91, 420, 185, 40);
    contentPane.add(txtInput);
    txtInput.setColumns(10);

    btnSend = new JButton("Send");
    btnSend.setBounds(288, 420, 76, 40);
    contentPane.add(btnSend);

    lblUserName = new JLabel("Name");
    lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
    lblUserName.setBounds(12, 420, 67, 40);
    contentPane.add(lblUserName);
    setVisible(true);
    
    btnHit = new JButton("Hit");
    btnHit.setBounds(550, 420, 76, 40);
    contentPane.add(btnHit);
    
    btnStay = new JButton("Stay");
    btnStay.setBounds(650, 420, 76, 40);
    contentPane.add(btnStay);
    
    btnExit = new JButton("Exit");
    btnExit.setBounds(750, 420, 76, 40);
    contentPane.add(btnExit);
    
    btnJoin = new JButton("Join");
    btnJoin.setBounds(370, 10, 76, 40);
    contentPane.add(btnJoin);
    
    btnStart = new JButton("Start");
    btnStart.setBounds(920, 10, 76, 40);
    contentPane.add(btnStart);
    
    layeredPane = new JLayeredPane();
    layeredPane.setBounds(385, 20, 590, 390);
    contentPane.add(layeredPane);
    
    gameFrame = new GameFrame();
    gameFrame.setBounds(0, 0, 590, 390);
    layeredPane.add(gameFrame, JLayeredPane.DEFAULT_LAYER);

    cardPanel = new CardPanel(username);
    cardPanel.setBounds(0, 0, 590, 390);
    layeredPane.add(cardPanel, JLayeredPane.PALETTE_LAYER);
    

    AppendText("User " + username + " connecting " + ip_addr + " " + port_no + "\n");
    UserName = username;
    lblUserName.setText(username + ">");

    try {
      socket = new Socket(ip_addr, Integer.parseInt(port_no));
      is = socket.getInputStream();
      dis = new DataInputStream(is);
      os = socket.getOutputStream();
      dos = new DataOutputStream(os);

      SendMessage("/login " + UserName);
      ListenNetwork net = new ListenNetwork();
      net.start();
      Myaction action = new Myaction();
      btnSend.addActionListener(action); // 내부클래스로 액션 리스너를 상속받은 클래스로
      btnExit.addActionListener(action);
      btnJoin.addActionListener(action);
      btnStart.addActionListener(action);
      btnStay.addActionListener(action);
      btnHit.addActionListener(action);
      txtInput.addActionListener(action);
      txtInput.requestFocus();
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
      AppendText("connect error");
    }
  }

  // Server Message를 수신해서 화면에 표시
  class ListenNetwork extends Thread {
	    public void run() {
	        while (true) {
	            try {
	                // Use readUTF to read messages
	                String msg = dis.readUTF();
	                AppendText(msg);
	                
	                // 카드 정보 처리 로직 추가
	                String[] parts = msg.split(" ");

	                // 첫 번째 부분은 명령어입니다.
	                String command = parts[0];

	                if (command.equals("/new_dealer_card")) {
	                    // 두 번째 부분은 플레이어의 ID입니다.
	                    // 세 번째 부분은 카드의 무늬입니다.
	                    // 네 번째 부분은 카드의 숫자입니다.
	                    String playerId = parts[1];
	                    String suit = parts[2];
	                    String rank = parts[3].trim();

	                    // 카드를 화면에 그립니다.
	                    cardPanel.drawCard("dealer", suit, rank);
	                }else if (command.equals("/new_card")) {
	                    // 두 번째 부분은 플레이어의 ID입니다.
	                    // 세 번째 부분은 카드의 무늬입니다.
	                    // 네 번째 부분은 카드의 숫자입니다.
	                    String playerId = parts[1];
	                    String suit = parts[2];
	                    String rank = parts[3].trim();;

	                    // 카드를 화면에 그립니다.
	                    cardPanel.drawCard(playerId, suit, rank);
	                } else if (command.equals("/dealer_card_open")) {
	                    // 두 번째 부분은 플레이어의 ID입니다.
	                    // 세 번째 부분은 카드의 무늬입니다.
	                    // 네 번째 부분은 카드의 숫자입니다.
	                    String suit = parts[1];
	                    String of = parts[2];
	                    String rank = parts[3].trim();;

	                    // 카드를 화면에 그립니다.
	                    cardPanel.drawCard("dealer", suit, rank);
	                } else if (msg.startsWith("/game_end")) {
	                    JOptionPane.showMessageDialog(null, "게임이 종료되었습니다. 다시 게임을 하신다면 Join 버튼을 눌러주세요.", "게임 종료", JOptionPane.WARNING_MESSAGE);
	                    cardPanel.reset(); // 카드 패널을 초기화합니다.
	                }
	                else if (msg.startsWith("/bust")) {
	                	String Playername = parts[1].split(":")[0].substring(1);
	                    JOptionPane.showMessageDialog(null, Playername + "님이(가) 파산했습니다.", "플레이어 파산", JOptionPane.WARNING_MESSAGE);  
	                }
	                else if (msg.startsWith("/game_win")) {
	                	String Playername = parts[1].split(":")[0].substring(1);
	                    JOptionPane.showMessageDialog(null, Playername + "님이(가) 우승했습니다.", "플레이어 우승", JOptionPane.WARNING_MESSAGE);  
	                }
	                else if (msg.startsWith("/dealer_stay")) {
	                	String Playername = parts[1].split(":")[0].substring(1);
	                    JOptionPane.showMessageDialog(null, "Dealer가 우승했습니다.", "Dealer 우승", JOptionPane.WARNING_MESSAGE);  
	                }

	            } catch (IOException e) {
	                AppendText("dis.read() error");
	                try {
	                    dos.close();
	                    dis.close();
	                    socket.close();
	                    break;
	                } catch (Exception ee) {
	                    break;
	                }
	            }
	        }
	    }
	}


  // keyboard enter key 치면 서버로 전송
  class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
  {
    @Override
    public void actionPerformed(ActionEvent e) {
      // Send button을 누르거나 메시지 입력하고 Enter key 치면
      if (e.getSource() == btnSend || e.getSource() == txtInput) {
        String msg = null;
        msg = txtInput.getText();
        SendMessage(msg);
        txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
        txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
        if (msg.contains("/exit")) // 종료 처리
          System.exit(0);
      }
      if(e.getSource() == btnExit){
    	  System.exit(0);
      }
      if (e.getSource() == btnHit) {
    	    String msg = "/hit";
    	    txtInput.setText(msg); // 텍스트 필드에 메시지 설정
    	    SendMessage(msg);
    	    txtInput.setText(""); // 메시지를 전송한 후 텍스트 필드 비움
    	}

    	if (e.getSource() == btnStay) {
    	    String msg = "/stay";
    	    SendMessage(msg);
    	}
    	if (e.getSource() == btnJoin) {
    	    String msg = "/join";
    	    SendMessage(msg);
    	}

    	if (e.getSource() == btnStart) {
    	    String msg = "/start";
    	    SendMessage(msg);
    	}
    }
  }

  // 화면에 출력
  public void AppendText(String msg) {
    textArea.append(msg);
    textArea.setCaretPosition(textArea.getText().length());
  }

  // Server에게 network으로 전송
  public void SendMessage(String msg) {
	    try {
	        if (dos != null) {
	            dos.writeUTF(msg);
	            dos.flush();
	        } else {
	            AppendText("DataOutputStream이 초기화되지 않았습니다.");
	        }
	    } catch (IOException e) {
	        AppendText("메시지 전송 중 오류 발생: " + e.getMessage());
	        try {
	            dos.close();
	            dis.close();
	            socket.close();
	        } catch (IOException e1) {
	            e1.printStackTrace();
	            System.exit(0);
	        }
	    }
	}
}
