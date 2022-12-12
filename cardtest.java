package finalTerm;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridLayout;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class cardtest {
	int port; //각 부서별 포트 번호 저장
	boolean set;
	int myPort;
	MessengerA msg;
	String name;
	MyFrame f;
	String dbName;
	List<User> studentList;
	List<User> professorList;
	List<String> studentNameList;
	List<String> professorNameList;
	
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
	
	public cardtest() throws IOException
	{
		dbName = "user_db";
		studentList = new ArrayList<>();
		studentNameList = new ArrayList<>();
		professorList = new ArrayList<>();
		professorNameList = new ArrayList<>();
		readLog();
		f = new MyFrame();
		
	}
	void getMyPortAndName(int myPort, String name)
	{
		this.myPort = myPort;
		this.name = name;
		
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
					studentList.add(user);
				}
				rs = stmt.executeQuery("select * from student_table");
				while (rs.next())
				{
					String user = new String(rs.getString("uid"));
					studentNameList.add(user);
				}

				// 교수 테이블에서 불러오기.
				rs = stmt.executeQuery("select * from professor_table");
				while (rs.next())
				{
					User user = new User(rs.getInt("id")+5000, rs.getString("uid"), rs.getString("pwd"));
					professorList.add(user);
				}
				rs = stmt.executeQuery("select * from professor_table");
				while (rs.next())
				{
					String user = new String(rs.getString("uid"));
					professorNameList.add(user);
				}
			} catch(SQLException e) {
				System.out.println(e.getErrorCode());
				System.exit(0);
			}
		}

	class MyFrame extends JFrame implements ActionListener{
		User[] professor =professorList.toArray(new User[0]);
		String[] professorName = professorNameList.toArray(new String[0]);
		User[] student = studentList.toArray(new User[0]);
		String[] studentName = studentNameList.toArray(new String[0]);
		int index = 0;
		JPanel panel;
		Cards cards;

		public MyFrame() {
			setTitle("부서별 인원선택");
			setSize(320,120);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			panel=new JPanel(new GridLayout(0,4,5,0));
			addButton("<",panel); //이전 카드를 확인할 수 있는 버튼
			addButton(">",panel); //이후 카드를 확인할 수 있는 버튼
			addButton("Select",panel);
			addButton("Exit",panel); //종료하기 위한 버튼
			add(panel,"South");

			cards = new Cards();
			add(cards,"Center");

			setVisible(false);
		}
		void addButton(String str, Container target){
			JButton button=new JButton(str);
			button.addActionListener(this);
			target.add(button);
		}
		protected class Cards extends JPanel{ //CardLayout 생성
			CardLayout layout;
			JPanel panel_1,panel_2; //교수와 학생을 나타내는 panel 생성
			JLabel label_1,label_2;

			JComboBox professorCombo,userCombo; //부서별 인원들을 나타내기 위한 콤보박스 지정
			public Cards() {
				layout=new CardLayout();
				setLayout(layout);

				JComboBox professorCombo= new JComboBox(professorName);
				JComboBox userCombo= new JComboBox(studentName);

				panel_1=new JPanel();
				panel_1.setLayout(new BoxLayout(panel_1,BoxLayout.Y_AXIS));
				label_1=new JLabel("교수");
				panel_1.add(label_1);
				panel_1.add(professorCombo);

				panel_2=new JPanel();
				panel_2.setLayout(new BoxLayout(panel_2,BoxLayout.Y_AXIS));
				label_2=new JLabel("학생");
				panel_2.add(label_2);
				panel_2.add(userCombo);

				add(panel_1);
				add(panel_2);

				//교수 
				professorCombo.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						index=professorCombo.getSelectedIndex();
						port = professor[index].id;
						System.out.println(professorCombo.getSelectedItem().toString()+"의 포트번호는 "+port+"입니다.");
					}
				});
				//학생 
				userCombo.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						index = userCombo.getSelectedIndex();
						port = student[index].id;
						System.out.println(userCombo.getSelectedItem().toString()+"의 포트번호는 "+port+"입니다.");
					}
				});
			}
		}
		public void actionPerformed(ActionEvent e){
			if(e.getActionCommand().equals("Exit")) {
				System.exit(0);
			} else if(e.getActionCommand().equals("<")) {
				cards.layout.previous(cards);
			} else if(e.getActionCommand().equals(">")) {
				cards.layout.next(cards);
			} else if(e.getActionCommand().equals("Select")) {
				dispose(); //현재 창을 닫고 이후 messenger 실행하면 됨 //현재 port에 값이 저장이 되어있는 상태
				try {
					msg = new MessengerA(myPort, port, name);
					set = true;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}