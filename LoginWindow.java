package finalTerm;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginWindow{
	protected JTextField idTextField;
	protected JPasswordField pwdPasswordField;
	String dbName;
	protected JPanel idPanel;
	protected JPanel buttonPanel;
	protected JButton loginButton;
	protected JButton addUserButton;
	String ruid;
	boolean set;// MenseengerA visible 설정.
	cardtest card;
	int myPort;
	MyFrame f;

	// 유저 클래스 
	class User{
		int id;
		String uid;
		String pwd;

		public User(int id, String uid,String pwd) {
			this.id = id;
			this.uid = uid;
			this.pwd = pwd;
		}
		public User(String uid, String pwd)
		{
			this.uid = uid;
			this.pwd = pwd;
			this.id = 0;
		}

		public String getUid()
		{
			return uid;
		}
	}

	List<User> userList;

	// 생성자.
	public LoginWindow() throws IOException{
		card = new cardtest();
		f = new MyFrame();
		dbName = "user_db";
		userList = new ArrayList<>();
		readLog();
	}

	public void startFrame() {
		MyFrame f = new MyFrame();
	}
	// 유저 DB와 연결.
	public static Connection makeConnection(String dbName) {
		String url = "jdbc:mariadb://localhost:3306/" + dbName;
		String id = "root";
		String password ="1234";
		Connection con= null;

		try {
			Class.forName("org.mariadb.jdbc.Driver");// 드라이버 클래스 적재 : 지정된 이름의 클래스를 찾아서 메모리로 적재.
			System.out.println("드라이버 적재 성공" );
			con =DriverManager.getConnection(url, id, password);// 사용자 아이디 패스워드를 사용해 데이터베이스 연결.
			System.out.println("데이터베이스 연결 성공 ");
		}catch(ClassNotFoundException e) {
			System.out.println("드라이버를 찾을 수 없습니다.");
		}catch(SQLException e) {
			System.out.println("연결에 실패하였습니다.");
		}
		return con;
	}

	// DB 내용을저장.
	public void readLog() {
		Connection con = makeConnection(dbName);
		int count = 0;
		try {

			Statement stmt = con.createStatement();
			// 학생 테이블에서 불러오기.
			ResultSet rs = stmt.executeQuery("select * from student_table");
			while (rs.next())
			{
				User user = new User(rs.getInt("id")+6000, rs.getString("uid"), rs.getString("pwd"));
				userList.add(user);
			}

			// 교수 테이블에서 불러오기.
			rs = stmt.executeQuery("select * from professor_table");
			while (rs.next())
			{
				User user = new User(rs.getInt("id")+5000, rs.getString("uid"), rs.getString("pwd"));
				userList.add(user);
			}
		} catch(SQLException e) {
			System.out.println(e.getErrorCode());
			System.exit(0);
		}
	}

	// 로그인 인터페이스
	class MyFrame extends JFrame implements ActionListener{
		public MyFrame() {

			setTitle("로그인 ");
			Container con = getContentPane();
			setLayout(new BoxLayout(con , BoxLayout.Y_AXIS));
			idPanel = new JPanel();
			buttonPanel = new JPanel();
			add(idPanel);
			add(buttonPanel);

			idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS));

			idPanel.add(new JLabel("아이디"));
			idTextField = new JTextField(20);
			idPanel.add(idTextField);
			idPanel.add(new JLabel("비밀번호"));
			pwdPasswordField = new JPasswordField(20);
			pwdPasswordField.setEchoChar('*');
			idPanel.add(pwdPasswordField);

			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

			loginButton = new JButton("로그인");
			buttonPanel.add(loginButton);

			loginButton.addActionListener(this);

			pack();
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}

		// 리스트 확인 메서드.
		boolean checkList(String uid, String pwd){
			Iterator<User> it = userList.iterator();
			while (it.hasNext()) {
				User u = it.next();
				if (u.uid.equals(ruid) && u.pwd.equals(pwd)) {
					myPort = u.id;
					return true;
				}
			}
			return false;
		}

		public void actionPerformed(ActionEvent evt) {
			ruid = idTextField.getText();
			String realPwd = "";
			char[] pwd = pwdPasswordField.getPassword();
			
			for(char cha : pwd)
			{
				Character.toString(cha);
				realPwd +=(realPwd.equals(""))?""+cha+"" : ""+cha+"";
			}

			if(evt.getSource() == loginButton) {
				if (checkList(ruid,realPwd)) {
					this.dispose();
					card.getMyPortAndName(myPort, ruid);
					card.f.setVisible(true);
					System.out.println(ruid);
					System.out.println(myPort);
				}

			}
		}
	}
}

