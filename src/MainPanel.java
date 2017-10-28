import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;


public class MainPanel extends JFrame {
	private JMenuBar mb;
	private JMenu mFile, mHelp;
	private JMenuItem  miInfo, miExit, miHelpCon, miClear, miNick;
	private JTextArea area;
	private JTextField field;
	private JButton input;
	private JScrollPane sp;
	private String nName, echo, sNum;
	private DataOutputStream out;
	private Socket so;
	private inputListener inp = new inputListener();
	private int stn; 
	public MainPanel(String hyunsu, int jaemoon) {
		super("Random Chating");
		sNum = hyunsu;
		stn = jaemoon;
		String[] options = {"OK"};
		JPanel panel = new JPanel();
		JLabel nickName = new JLabel("닉네임 : ");
		JTextField name = new JTextField(10);
		panel.add(nickName);
		panel.add(name);
		int selectedOption = JOptionPane.showOptionDialog
				(null, panel, "닉네임 설정", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
		if(selectedOption == 0){
			nName = name.getText();
		}
		if(nName.equals("")){
			nName = "손님";
		}
		try{
			
			Socket s = new Socket("127.0.0.1", 8522);
			so=s;
			out = new DataOutputStream(so.getOutputStream());
			setLayout(new BorderLayout(10,10));
			input = new JButton("send");
			area = new JTextArea();
			field = new JTextField(38);
			JPanel bottom = new JPanel(new BorderLayout());
			Font font =new Font("굴림", Font.BOLD, 11);
			input.setFont(font);
			input.setEnabled(false);
			sp = new JScrollPane(area);
			bottom.add("Center",field);
			bottom.add("East", input);
			mb = new JMenuBar();
			mFile = new JMenu("File");
			mHelp = new JMenu("Help");
			miInfo = new JMenuItem("Information");
			miNick = new JMenuItem("Nickname");
			miExit = new JMenuItem("Exit");
			miHelpCon = new JMenuItem("Help Content");
			miClear = new JMenuItem("Clear");
			mFile.add(miClear);
			mFile.add(miInfo);
			mFile.add(miNick);
			mFile.addSeparator();
			mFile.add(miExit);
			mHelp.add(miHelpCon);
			mb.add(mFile);
			mb.add(mHelp);
			this.setJMenuBar(mb);
			add("Center", sp);
			add("South", bottom);
			miInfo.addActionListener(new MenuListener());
			miNick.addActionListener(new MenuListener());
			miClear.addActionListener(new MenuListener());
			miExit.addActionListener(new MenuListener());
			miHelpCon.addActionListener(new MenuListener());
			field.addActionListener(inp);
			input.addActionListener(inp);
			field.addKeyListener(new Klistener());
			area.setEditable(false);
			setSize(new Dimension(500, 300));
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screenSize.width-500)/2, (screenSize.height-300)/2);
			field.requestFocus();
			setVisible(true);
			Thread sender = new Thread(new ClientSender(s, nName));
			Thread receiver = new Thread(new ClientReceiver(s));
			sender.start();
			receiver.start();
			
		}catch(Exception e){}
		
	}
	class ClientSender extends Thread{
		Socket socket;
		DataOutputStream out;
		String name;
		ClientSender(Socket socket, String name){
			this.socket = socket;
			try {
				out = new DataOutputStream(socket.getOutputStream());
				this.name = name;
			}catch(Exception e){}
		}
		public void run() {
			try{
				if(out!=null){
					out.writeUTF(name);
				}
				while(out!=null){}
			}catch(IOException e){}
		}
	}
	class ClientReceiver extends Thread{
		Socket socket;
		DataInputStream in;
		ClientReceiver(Socket socket){
			this.socket = socket;
			try{
				in = new DataInputStream(socket.getInputStream());
			}catch(IOException e){}
		}
		public void run(){
			while(in!=null){
				try{
					echo = in.readUTF();
					if(!echo.equals("없음")&&!echo.equals("끝")){
						area.append(echo+"\n");
						sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());
					}
					if(echo.equals("없음")) break;
					if(echo.equals("끝")) break;
						
				}catch(IOException e){}
			}
			while(in!=null){
				try {
					echo = in.readUTF();
					area.append(echo+"\n");
					sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum()); // 스크롤 자동으로 내리기
				
				} catch (IOException e) {
				
				}
			}
		}
	}
	private  class inputListener implements ActionListener {
		public void actionPerformed (ActionEvent event){
			try {
				if(stn==-1){
					out.writeUTF(nName+" : "+field.getText()+"( 학번: "+sNum+"-"+"자리예약안함"+")");
					field.setText("");
					field.requestFocus();
				}else{	
					out.writeUTF(nName+" : "+field.getText()+"( 학번: "+sNum+" - "+(stn+1)+"번자리)");
					field.setText("");
					field.requestFocus();
				}
			} catch (IOException e) {}
		}
	}

	private class MenuListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();
			 if(str.equals("Information")){
				JOptionPane.showMessageDialog(null, "Copyright(c)2015 by 이재문&임현수.\n"
												+"All rights reserved.\n", "만든이", JOptionPane.INFORMATION_MESSAGE);
			}else if(str.equals("Clear")){
				area.setText("");
			}else if(str.equals("Exit")){
				int result=0;
				result = JOptionPane.showConfirmDialog(null, "종료하시겠습니까?", "종료", JOptionPane.YES_NO_OPTION);
				if(result==0){
					System.exit(0);
				}
			}else if(str.equals("Nickname")){
				String[] options = {"OK"};
				JPanel panel = new JPanel();
				JLabel nickName = new JLabel("닉네임 : ");
				JTextField name = new JTextField(10);
				panel.add(nickName);
				panel.add(name);
				int selectedOption = JOptionPane.showOptionDialog
						(null, panel, "닉네임 변경", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
				if(selectedOption == 0){
					nName = name.getText();
				}
				if(nName.equals("")){
					nName="손님";
				}
			}else if(str.equals("Help Content")){
				String helping = "#랜덤채팅 도움말 페이지입니다.#\n";
				helping = helping + "1. 입력란에 글자를 입력합니다.\n";
				helping = helping + "2. Enter키를 누르거나, send버튼을 누릅니다.\n";
				helping = helping + "3. 상대와 즐겁게 채팅을 합니다.\n";
				helping = helping + "* Clear - 대화내용을 지움\n";
				helping = helping + "* Information - 만든이 정보\n";
				helping = helping + "* Exit - 방 나가기";
				JOptionPane.showMessageDialog(null, helping, "도움말", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
	}
	private class Klistener implements KeyListener {
		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {
			 if(field.getText().equals("")) input.setEnabled(false);
	         else input.setEnabled(true);
		}
		public void keyTyped(KeyEvent e) {}	
	}
}