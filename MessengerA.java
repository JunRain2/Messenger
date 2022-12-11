package finalTerm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MessengerA {
	protected JTextField textField;
	protected JTextArea textArea;
	DatagramSocket socket;
	DatagramPacket messagePacket;
	InetAddress address = null;
	int myPort;// 수신용 포트. 
	int otherPort;// 송신용 포트. 
	String name;// 사용자 이름.
	String dbName;
	MyFrame f;

	// 자신의 주소를 생성자를 통해 생성.
	public MessengerA(int myPort, int otherPort, String name) throws IOException{

		this.myPort = myPort;
		this.otherPort = otherPort;
		this.name = name;
		dbName = "msg_db";
		f = new MyFrame();
		address = InetAddress.getByName("192.168.45.228");
		socket = new DatagramSocket(myPort);// UDP 프로토콜을 사용하는 소켓을 생성.
	}


	// 메시지 DB와 연결.
	public static Connection makeConnection(String dbName) {
		String url = "jdbc:mariadb://localhost:3306/" + dbName;
		String id = "root";
		String password ="1234";
		Connection con= null;

		try {
			Class.forName("org.mariadb.jdbc.Driver");// 드라이버 클래스 적재 : 지정된 이름의 클래스를 찾아서 메모리로 적재.
			System.out.println("드라이버 적재 성공");
			con =DriverManager.getConnection(url, id, password);// 사용자 아이디 패스워드를 사용해 데이터베이스 연결.
			System.out.println("데이터베이스 연결 성공");
		}catch(ClassNotFoundException e) {
			System.out.println("드라이버를 찾을 수 없습니다.");
		}catch(SQLException e) {
			System.out.println("연결에 실패하였습니다.");
		}
		return con;
	}
	// 메시지를 DB에 저장.
	public void saveLog(String log) {
		Connection con = makeConnection("msg_db");
		try {
			Statement stmt = con.createStatement();
			String s = "insert into msg_table (msg) values";
			s += "('" + log + "')";
			System.out.println(s);
			int i = stmt.executeUpdate(s);
			if(i==1)
				System.out.println("레코드 추가 성공");
			else 
				System.out.println("레코드 추가 실패");
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

	}

	// 패킷을 받아 텍스트 영역에 표시.
	public void process(){
		while(true){
			try {
				byte[] messageBuf = new byte[256];
				messagePacket = new DatagramPacket(messageBuf, messageBuf.length);
				socket.receive(messagePacket);
				String receiveMessage = new String(new String(messageBuf) + "\n");
				textArea.append(receiveMessage);// 받은 패킷을 영역에 표시.
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}


	// 내부 클래스 정의. 
	class MyFrame extends JFrame implements ActionListener {
		// 메신저 사용자 인터페이스.
		public MyFrame(){
			super(name);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			textField = new JTextField(30);
			textField.addActionListener(this);

			textArea = new JTextArea(10, 30);
			textArea.setEditable(false);

			add(textField, BorderLayout.PAGE_END);
			add(textArea, BorderLayout.CENTER);
			pack();
			setVisible(true);
		}

		// 이벤트 실행시 메시지 전송.
		public void actionPerformed(ActionEvent evt) {
			String s = textField.getText();
			s ="["+ name+"] :" + s;
			byte[] messageBuffer = s.getBytes();
			// 보내는 패킷.
			DatagramPacket messagePacket;

			// 패킷을 생성.
			messagePacket = new DatagramPacket(messageBuffer, messageBuffer.length, address, otherPort);// 패켓에 메시지를 담음.
			try {
				socket.send(messagePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sendMessege = s + "\n";   
			textArea.append(sendMessege);// 보낸 메시지를 텍스트 필드에 추가.
			saveLog(sendMessege);
			textField.selectAll();
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		LoginWindow lw = new LoginWindow();//각 클래스 필드를 연결해 process 실행.
		lw.card.msg.process();
	}


}
