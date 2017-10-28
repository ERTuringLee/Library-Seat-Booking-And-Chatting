import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;



public class LibraryClientPanel extends JFrame{
	private JPanel panel1, panel2;
	private JLabel text = new JLabel("���ϴ� �ڸ��� �����ϼ���!");
	private JButton exit = new JButton("���");
	private JButton chat = new JButton("ä��");
	private JButton[] seats = new JButton[100];
	private int stn=-1;
	private String sNum, seatNum, echo;
	private Integer igr, igr1, igr2;
	private boolean sb = false;
	private DataOutputStream out;
	private DataInputStream in;
	private Socket so;
	public LibraryClientPanel(){
		super("������ �ڸ����� ���α׷� v2.9");
		String[] options = {"OK"};
		JPanel panel = new JPanel();
		JLabel stNumIn = new JLabel("�й� : ");
		JTextField stNum = new JTextField(10);
		panel.add(stNumIn);
		panel.add(stNum);
		int selectedOption = JOptionPane.showOptionDialog
				(null, panel, "�г��� ����", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
		if(selectedOption == 0){
			sNum = stNum.getText();
		}
		while(sNum.equals("")){
			selectedOption = JOptionPane.showOptionDialog
					(null, panel, "�г��� ����", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
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
			panel2.setBorder(new TitledBorder("�ڸ� ����"));
			setSize(new Dimension(600, 400));
			add("Center",panel2);
			add("North", panel1);
			while(in!=null){
				seatNum = in.readUTF();
				if(!seatNum.equals("����")&&!seatNum.equals("��")){
					if(seatNum.substring(0, 2).equals("����")){
						igr1 =new Integer(seatNum.substring(2));
						stn = igr1.intValue();
						seats[stn].setEnabled(false);
						text.setText((stn+1)+"�� - ����� ���ҽ� ��� ��ư�� �����ּ���");
						sb = true;
					}else if(seatNum.substring(0, 2).equals("�ٸ�")){
						igr1 =new Integer(seatNum.substring(2));
						stn = igr1.intValue();
						seats[stn].setEnabled(false);
					}
				}
				if(seatNum.equals("����")) break;
				if(seatNum.equals("��")) break;
			}
			setVisible(true);
			Thread receiver = new Thread(new ClientReceiver(so));
			receiver.start();
		}catch(Exception e){}
	}
	public class Listener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();
			if(str!="���"&&str!="ä��"&&sb==false){
				igr = new Integer(str);
				stn = igr.intValue()-1;
				seats[stn].setEnabled(false);
				text.setText(igr+"�� - ����� ���ҽ� ��� ��ư�� �����ּ���");
				try {
					out.writeUTF("�Խ�"+igr);
				} catch (IOException e1) {}
				sb=true;
			}
			if(str=="���"&&sb==true){
				seats[stn].setEnabled(true);
				stn = -1;
				try {
					out.writeUTF("���");
				} catch (IOException e1) {}
				text.setText("���ϴ� �ڸ��� �����ϼ���!");
				sb=false;
			}
			if(str=="ä��"){
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
					if(echo.substring(0, 2).equals("�Խ�")){
						igr2= new Integer(echo.substring(2));
						int igen = igr2.intValue()-1;
						seats[igen].setEnabled(false);
					}
					if(echo.substring(0, 2).equals("���")){
						igr2= new Integer(echo.substring(2));
						int igen = igr2.intValue()-1;
						seats[igen].setEnabled(true);
					}

				}catch(IOException e){}
			}
		}
	}
	
}
