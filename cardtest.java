package finalTerm;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
	
	public cardtest(int myPort, String name)
	{
		MyFrame f = new MyFrame();
		this.myPort = myPort;
		this.name = name;
	}
	
	class MyFrame extends JFrame implements ActionListener{
		   String[] professor = {"노영주" , "나보균"};
		   String[] user = {"이준우", "김학선"};
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
		      
		      setVisible(true);
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
		         
		         JComboBox professorCombo=new JComboBox(professor);
		         JComboBox userCombo=new JComboBox(user);
		         
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
		               port=professorCombo.getSelectedIndex()+5000;
		               System.out.println(professorCombo.getSelectedItem().toString()+"의 포트번호는 "+port+"입니다.");
		            }
		         });
		         //학생 
		         userCombo.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		               port=userCombo.getSelectedIndex()+6000;
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
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		      }
		   }
		}
   }