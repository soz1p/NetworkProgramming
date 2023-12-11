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
    
    gameFrame = new GameFrame();
    gameFrame.setBounds(400, 20, 550, 400); // 예시로 너비와 높이를 500으로 설정했습니다.
    contentPane.add(gameFrame);
   

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
