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
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginWindow{
	protected JTextField idTextField;
	protected JTextField pwdTextField;
	String dbName;
	protected JPanel idPanel;
	protected JPanel buttonPanel;
	protected JButton loginButton;
	protected JButton addUserButton;
	String ruid;
	boolean set;// MenseengerA visible 설정.
	MessengerA msgA;

	// 유저 클래스 
	class User{
		String uid;
		String pwd;

		public User(String uid,String pwd) {
			this.uid = uid;
			this.pwd = pwd;
		}
		
		@Override
		public boolean equals(Object object) {
			User product = (User) object;
			if (product.uid.equals(this.uid) && product.pwd.equals(this.pwd)) {
				return true;
			}
			return false;
		}
	}

	List<User> userList;

	
	public LoginWindow(MessengerA msgA) throws IOException{
		dbName = "user_db";
		set = false;
		userList = new ArrayList<>();
		MyFrame f= new MyFrame();
		this.msgA = msgA;
		msgA.process();
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
			ResultSet rs = stmt.executeQuery("select * from user_table");
			while (rs.next())
			{
				User user = new User(rs.getString("uid"), rs.getString("pwd"));
				userList.add(user);
			}
		} catch(SQLException e) {
			System.out.println(e.getErrorCode());
			System.exit(0);

		}
	}

	// 회원가입 메소드.
	public void addUser(User addUser) {
		Connection con = makeConnection(dbName);
		try {
			Statement stmt = con.createStatement();
			String s = "insert into user_table values";
			s += "(null,'" + addUser.uid + "','" +addUser.pwd+ "')";
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
			pwdTextField = new JTextField(20);
			idPanel.add(pwdTextField);

			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

			loginButton = new JButton("로그인");
			buttonPanel.add(loginButton);
			addUserButton = new JButton("추가");
			buttonPanel.add(addUserButton);

			loginButton.addActionListener(this);
			addUserButton.addActionListener(this);


			readLog();

			pack();
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}

		// 리스트 확인 메서드.
		boolean checkList(String uid, String pwd)
		{
			for(User checkUser : userList) {
				if(checkUser.uid == uid && checkUser.pwd == pwd) {
					return true;
				}
			}
			return false;
		}

		public void actionPerformed(ActionEvent evt) {
			ruid = idTextField.getText();
			String pwd = pwdTextField.getText();

			User loginUser =  new User(ruid,pwd);

			// 로그인 버튼 MessengerA false -> true. 
			if(evt.getSource() == loginButton) {
				if(userList.contains(loginUser)){
					msgA.getterName(ruid);
					this.dispose();
					set = true;
					msgA.f.setVisible(true);
				}
				else System.out.println("로그인 실패");
			}
			
			// 회원가입 버튼. 
			else if(evt.getSource() == addUserButton){
				addUser(loginUser);
			}
		}
	}

}


