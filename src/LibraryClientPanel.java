import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;



public class LibraryClientPanel extends JFrame{
	private JPanel panel1, panel2;
	private JLabel text = new JLabel("원하는 자리를 선택하세요!");
	private JButton exit = new JButton("퇴실");
	private JButton chat = new JButton("채팅");
	private JButton[] seats = new JButton[100];
	private int stn=-1;
	private String sNum, seatNum, echo;
	private Integer igr, igr1, igr2;
	private boolean sb = false;
	private DataOutputStream out;
	private DataInputStream in;
	private Socket so;
	public LibraryClientPanel(){
		super("도서관 자리예약 프로그램 v2.9");
		String[] options = {"OK"};
		JPanel panel = new JPanel();
		JLabel stNumIn = new JLabel("학번 : ");
		JTextField stNum = new JTextField(10);
		panel.add(stNumIn);
		panel.add(stNum);
		int selectedOption = JOptionPane.showOptionDialog
				(null, panel, "닉네임 설정", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
		if(selectedOption == 0){
			sNum = stNum.getText();
		}
		while(sNum.equals("")){
			selectedOption = JOptionPane.showOptionDialog
					(null, panel, "닉네임 설정", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
			if(selectedOption == 0){
				sNum = stNum.getText();
			}
		}
		try{
			so = new Socket("127.0.0.1", 8523);
			out = new DataOutputStream(so.getOutputStream());
			in = new DataInputStream(so.getInputStream());
			out.writeUTF(sNum);
			setLayout(new BorderLayout(0, 0));
			panel1 = new JPanel(new FlowLayout(4));
			panel2 = new JPanel(new GridLayout(10,10));
			for(int i=0; i<seats.length; i++ ){
				seats[i] = new JButton(i+1+"");
				panel2.add(seats[i]);
				seats[i].addActionListener(new Listener());
			}
			panel1.add(text);
			panel1.add(exit);
			panel1.add(chat);
			exit.addActionListener(new Listener());
			chat.addActionListener(new Listener());
			panel2.setBorder(new TitledBorder("자리 선택"));
			setSize(new Dimension(600, 400));
			add("Center",panel2);
			add("North", panel1);
			while(in!=null){
				seatNum = in.readUTF();
				if(!seatNum.equals("없음")&&!seatNum.equals("끝")){
					if(seatNum.substring(0, 2).equals("같음")){
						igr1 =new Integer(seatNum.substring(2));
						stn = igr1.intValue();
						seats[stn].setEnabled(false);
						text.setText((stn+1)+"번 - 퇴실을 원할시 퇴실 버튼을 눌러주세요");
						sb = true;
					}else if(seatNum.substring(0, 2).equals("다름")){
						igr1 =new Integer(seatNum.substring(2));
						stn = igr1.intValue();
						seats[stn].setEnabled(false);
					}
				}
				if(seatNum.equals("없음")) break;
				if(seatNum.equals("끝")) break;
			}
			setVisible(true);
			Thread receiver = new Thread(new ClientReceiver(so));
			receiver.start();
		}catch(Exception e){}
	}
	public class Listener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();
			if(str!="퇴실"&&str!="채팅"&&sb==false){
				igr = new Integer(str);
				stn = igr.intValue()-1;
				seats[stn].setEnabled(false);
				text.setText(igr+"번 - 퇴실을 원할시 퇴실 버튼을 눌러주세요");
				try {
					out.writeUTF("입실"+igr);
				} catch (IOException e1) {}
				sb=true;
			}
			if(str=="퇴실"&&sb==true){
				seats[stn].setEnabled(true);
				stn = -1;
				try {
					out.writeUTF("퇴실");
				} catch (IOException e1) {}
				text.setText("원하는 자리를 선택하세요!");
				sb=false;
			}
			if(str=="채팅"){
				int stn2 = stn;
				MainPanel mp = new MainPanel(sNum, stn2);	
			}
		}
	}
	class ClientReceiver extends Thread{
		Socket socket;
		DataInputStream inp;
		ClientReceiver(Socket socket){
			this.socket = socket;
			try{
				inp = new DataInputStream(socket.getInputStream());
			}catch(IOException e){}
		}
		public void run(){
			while(inp!=null){
				try{
					echo = in.readUTF();
					if(echo.substring(0, 2).equals("입실")){
						igr2= new Integer(echo.substring(2));
						int igen = igr2.intValue()-1;
						seats[igen].setEnabled(false);
					}
					if(echo.substring(0, 2).equals("퇴실")){
						igr2= new Integer(echo.substring(2));
						int igen = igr2.intValue()-1;
						seats[igen].setEnabled(true);
					}

				}catch(IOException e){}
			}
		}
	}
	
}
