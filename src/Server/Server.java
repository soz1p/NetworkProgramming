package Server;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.util.HashSet;
import java.util.Random;

public class Server extends JFrame {

  private static final long serialVersionUID = 1L;
  private JPanel content_panel;
  JTextArea output;
  private JTextField port_number;

  private ServerSocket socket;
  private Socket client_socket;
  private Vector<UserConnection> Users = new Vector<>();
  private static final int BUF_LEN = 128;

  private int counter = 0; // counter for identify users

  private Game game = new Game();

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Server frame = new Server();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Server() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 338, 386);
    content_panel = new JPanel();
    content_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(content_panel);
    content_panel.setLayout(null);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(12, 10, 300, 244);
    content_panel.add(scrollPane);

    output = new JTextArea();
    output.setEditable(false);
    scrollPane.setViewportView(output);

    JLabel lblNewLabel = new JLabel("Port Number");
    lblNewLabel.setBounds(12, 264, 87, 26);
    content_panel.add(lblNewLabel);

    port_number = new JTextField();
    port_number.setHorizontalAlignment(SwingConstants.CENTER);
    port_number.setText("30000");
    port_number.setBounds(111, 264, 199, 26);
    content_panel.add(port_number);
    port_number.setColumns(10);

    JButton start_btn = new JButton("Start Listen");
    start_btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          socket = new ServerSocket(Integer.parseInt(port_number.getText()));
        } catch (NumberFormatException | IOException e1) {
          System.out.println("ERR>>Faild to parse port number");
          e1.printStackTrace();
        }
        print_server("Blackjack Server Running..");
        start_btn.setText("Blackjack Server Running..");
        start_btn.setEnabled(false);

        port_number.setEnabled(false);

        AcceptServer accept_server = new AcceptServer();
        accept_server.start();
      }
    });
    start_btn.setBounds(12, 300, 300, 35);
    content_panel.add(start_btn);
  }

  // accept new client and create user client thread
  class AcceptServer extends Thread {
    public void run() {
      while (true) {
        try {
          print_server("Waiting for clients ...");
          client_socket = socket.accept();

          print_server("New Client from " + client_socket);

          // spawn new thread for user
          UserConnection new_user = new UserConnection(client_socket, counter++);

          Users.add(new_user);
          print_server("Clients's count is are :" + Users.size());

          new_user.start(); // start user handling

        } catch (IOException e) {
          print_server("ERR>>AcceptServer: Faild to connect with user");
          e.printStackTrace();
        }
      }
    }
  }

  // append given str to output
  public void print_server(String str) {
    output.append(str + "\n");
    // move cursor to end
    output.setCaretPosition(output.getText().length());
  }

  class UserConnection extends Thread {
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket client_socket;
    private Vector<UserConnection> user_vc;
    private String user_name = "";
    private int id;
    private Hand hand;

    public UserConnection(Socket client_socket, int user_id) {
      this.client_socket = client_socket;
      this.user_vc = Users;
      this.id = user_id;

      try {
        is = client_socket.getInputStream();
        dis = new DataInputStream(is);
        os = client_socket.getOutputStream();
        dos = new DataOutputStream(os);

        // first_msg have to be like "/login {username}"
        String first_msg = dis.readUTF();
        print_server("first_msg:" + first_msg);
        String[] msg = first_msg.split(" ");
        if (!msg[0].equals("/login")) {
          throw new Exception();
        }
        user_name = msg[1].trim();

        print_server("User {" + user_name + "}, Id {" + this.id + "} has entered");
        send_single("Welcome to Blackjack room, " + user_name);

        send_all("/new_user_enter " + to_str());

        for (int i = 0; i < user_vc.size(); i++) {
          UserConnection user = user_vc.get(i);
          if (user.id != this.id) {
            send_single("/user_exists" + user.to_str());
          }
        }

        if (game.is_started) {
          send_single("/game_had_started");
        }

      } catch (Exception e) {
        print_server("ERR>>faild to handle connection");
        print_server("User {" + user_name + "}, Id {" + this.id + "} faild to enter");
        e.printStackTrace();
        disconnect();
      }
    }

    public String to_str() {
      return "{" + this.user_name + ":" + this.id + "}";
    }

    /// send msg to single client
    public void send_single(String msg) {
        try {
            if (dos != null) {
                dos.writeUTF(msg + "\n");
            } else {
                print_server("dos is null");
                // Re-initialize 'dos' if necessary
            }
        } catch (IOException e) {
        	disconnect();
            print_server("dos.write() error");
            System.exit(0);
            // Reconnect to the server if necessary
        }
    }

    /// send msg to all client
    public void send_all(String str) {
      for (int i = 0; i < user_vc.size(); i++) {
        UserConnection user = user_vc.get(i);
        user.send_single(str);
      }
    }

    /// find user by id
    public UserConnection find_user(int tgt_id) {
      for (int i = 0; i < user_vc.size(); i++) {
        UserConnection user = user_vc.get(i);
        if (user.id == tgt_id) {
          return user;
        }
      }
      return null;
    }

    /// handle disconnection of user
    public void disconnect() {
      try {
        dos.close();
        dis.close();
        client_socket.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      quit_game();
      send_all("/disconnect " + to_str());
      // remove this user from users
      Users.removeElement(this);
      print_server("User " + to_str() + " quited.\nCurrent user count" + Users.size() + "\n");
    }

    /// join game
    public void join_game() {
      if (game.join_game(this.id)) {
        send_all("/game_joined " + this.to_str());
        if (game.host_id == this.id) {
          send_all("/new_host " + this.id);
        }
      } else {
        send_single("/err game already started");
      }
    }

    public void quit_game() {
      if (game.participants.contains(this.id)) {
        game.quit_game(this.id);
        send_all("/game_quited " + this.to_str());
        if (this.id == game.dealer_id) {
          // if quited user is dealer, have to end the game
          check_result(-1);
        } else {
          send_all("/new_host " + game.host_id);
        }
      } else {
        send_single("/err you are not a participant");
      }
    }

    /// start game
    public void start_game() {
      if (game.host_id == this.id) {
        if (game.start_game()) {
          send_all("/start_game");
          // give cards to dealer first
          send_all("/new_dealer " + game.dealer_id);
          UserConnection dealer = find_user(game.dealer_id);
          print_server("Give cards to dealer...");
          dealer.get_cards_dealer();

          // give cards to players
          for (int player_id : game.participants) {
            if (player_id != game.dealer_id) {
              UserConnection player = find_user(player_id);
              player.get_cards();
            }
          }

        } else {
          send_single("/err faild to start game: too few player (n<2)");
        }
      } else {
        send_single("/err faild to start game: you are not a host");
      }
    }

    public void get_cards_dealer() {
      Card a = game.deck.deal();
      Card b = game.deck.deal();
      send_all("/new_dealer_card" + to_str());
      print_server(to_str() + "/new_dealer_card " + a.to_str());
      print_server(to_str() + "/new_dealer_card " + b.to_str());
      this.hand = new Hand(a, b);
    }

    public void get_cards() {
      Card a = game.deck.deal();
      Card b = game.deck.deal();
      send_all("/new_card " + to_str() + a.to_str());
      send_all("/new_card " + to_str() + b.to_str());
      print_server(to_str() + "/new_card " + a.to_str());
      print_server(to_str() + "/new_card " + b.to_str());
      this.hand = new Hand(a, b);
      send_all("/total " + to_str() + hand.get_total());
    }

    public void hit() {
      if (game.participants.contains(this.id)) {
        if (this.hand.check_bust()) {
          send_single("/err you are already busted");
        } else {
          if (this.hand.stay) {
            send_single("/err you are already stayed");
          } else {
            if (this.id == game.dealer_id) {
              // if user is dealer
              if (game.player_count != 0) {
                send_single("/err not your turn (now is player turn)");
              } else {
                Card a = game.deck.deal();
                send_all("/new_dealer_card " + to_str() + a.to_str());
                this.hand.hit(a);
                send_all("/dealer_total " + to_str() + hand.get_total());
                if (this.hand.check_bust()) {
                  send_all("/dealer_bust " + to_str());
                  check_result(-1);
                }
              }
            } else {
              // if user is player
              Card a = game.deck.deal();
              send_all("/new_card " + to_str() + a.to_str());
              this.hand.hit(a);
              send_all("/total " + to_str() + hand.get_total());
              if (this.hand.check_bust()) {
                send_all("/bust " + to_str());
                game.player_count--;
                if (game.player_count == 0) {
                  send_all("/dealer_turn_start");
                  dealer_turn_start();
                }
              }
            }
          }
        }
      } else {
        send_single("/err you are not joined to game");
      }
    }

    public void stay() {
      if (game.participants.contains(this.id)) {
        if (this.hand.check_bust()) {
          send_single("/err you are already busted");
        } else {
          if (this.hand.stay) {
            send_single("/err you are already stayed");
          } else {
            if (this.id == game.dealer_id) {
              // if user is dealer
              if (game.player_count != 0) {
                send_single("/err not your turn (now is player turn)");
              } else {
                if (this.hand.get_total() < 16) {
                  send_single("/err dealer must hit (till 16)");
                } else {
                  send_all("/dealer_stay");
                  check_result(this.hand.get_total());
                }
              }

            } else {
              // if user is player
              send_all("/stay " + to_str());
              game.player_count--;
              if (game.player_count == 0) {
                send_all("/dealer_turn_start");
                dealer_turn_start();
              }
            }
          }
        }
      } else {
        send_single("/err you are not joined to game");
      }
    }

    public void dealer_turn_start() {
      send_all("/dealer_card_open " + game.deck.deck[0].to_str());
      send_all("/dealer_card_open " + game.deck.deck[1].to_str());
    }

    // check result of game
    public void check_result(int dealer_total) {
      for (int i = 0; i < user_vc.size(); i++) {
        UserConnection user = user_vc.get(i);
        // find all participants
        if (game.participants.contains(user.id)) {
          // skip dealer
          if (game.dealer_id != user.id) {
            // skip busted
            if (!user.hand.check_bust()) {
              int user_total = user.hand.get_total();
              if (user_total > dealer_total) {
                send_all("/game_win " + user.to_str());
              } else if (user_total == dealer_total) {
                send_all("/game_draw " + user.to_str());
              } else {
                send_all("/game_lose " + user.to_str());
              }
            }
          }
        }
      }
      send_all("/game_end");
      game.end_game();
    }

    public void run() {
      while (true) {
        try {
          String user_msg = dis.readUTF();
          print_server(user_msg);
          String[] msg = user_msg.split(" ", 2);
          if (msg[0].equals("/join")) {
            join_game();
          } else if (msg[0].equals("/quit")) {
            quit_game();
          } else if (msg[0].equals("/start")) {
            start_game();
          } else if (msg[0].equals("/hit")) {
            hit();
          } else if (msg[0].equals("/stay")) {
            stay();
          } else if (msg[0].equals("/msg")) {
            send_all("/msg " + to_str() + msg[1] + "\n");
          }
        } catch (IOException e) {
          System.exit(0);
          print_server("dis.readUTF() error");
          disconnect();
          return;
        }
      }
    }
  }

  class Game {
    public int host_id = -1; // how has permission to start game
    public int dealer_id = -1;

    public int player_count = 0;
    public Deck deck = new Deck();

    private boolean is_started = false;

    public HashSet<Integer> participants = new HashSet<>();

    public boolean join_game(int user_id) {
      if (!is_started) {
        participants.add(user_id);
        if (host_id == -1) {
          host_id = user_id;
        }

        return true;
      } else {
        print_server("ERR>> Game already started \nSo user id{" + user_id + "} cannot join");
        return false;
      }
    }

    public void quit_game(int user_id) {

      if (!is_started) {
        // remove user from participants
        participants.remove(user_id);
        // pick new host
        if (host_id == user_id) {
          if (participants.isEmpty()) {
            host_id = -1;
          } else {
            host_id = random_pick();
          }
        }
      } else {
        // if game have started and user has joind,
        if (participants.contains(user_id)) {

          // if he is player
          if (dealer_id != user_id) {
            // reduce player_count
            player_count--;
          }
          participants.remove(user_id);
        }
      }
    }

    public boolean start_game() {
      if (this.participants.size() < 2) {
        return false;
      } else {
        this.is_started = true;
        deck.shuffle();
        this.dealer_id = random_pick();
        this.player_count = participants.size() - 1;
        return true;
      }
    }

    public int random_pick() {
      int size = participants.size();
      int rand = new Random().nextInt(size);
      int i = 0;
      for (int pick : participants) {
        if (i == rand) {
          return pick;
        }
        i++;
      }
      // cannot reach here
      return i;
    }

    public void end_game() {
      this.is_started = false;
      this.host_id = -1;
      this.dealer_id = -1;
      this.participants = new HashSet<>();
    }
  }

}
