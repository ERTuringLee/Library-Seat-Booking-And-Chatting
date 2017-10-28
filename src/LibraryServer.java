import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LibraryServer extends JFrame{
	ArrayList<String> seat= new ArrayList<String>();
	ArrayList<String> studentNum = new ArrayList<String>();
	HashMap<String, DataOutputStream> clients;
	JTextField field1, field2;
	JTextArea area;
	JScrollPane sp;
	ServerSocket serverSocket;
	LibraryServer(){
		super("관리자");
		setSize(200,300);
		setLocation(1000,100);
		setLayout(new BorderLayout(0,0));
		field1 = new JTextField("학번\t좌석");
		field1.setEditable(false);
		area= new JTextArea();
		area.setEditable(false);
		field2 = new JTextField();
		sp = new JScrollPane(area);
		add("North", field1);
		add("Center", area);
		add("South", field2);
		clients = new HashMap<String, DataOutputStream>();
		Collections.synchronizedMap(clients);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){ 
				System.exit(0);
			}
		});
		setVisible(true);
	}
	public static void main(String[] arg) {
		new LibraryServer().start();
		
	}
	public void start(){
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(8523);
			while(true){
				socket =serverSocket.accept();
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
				}
			}catch(Exception e){}
		}
	class ServerReceiver extends Thread{
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		
		ServerReceiver(Socket socket){
			this.socket = socket;
			try {
				in=new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			}catch(IOException e){}
		}
		public void run(){
			String name = "";
			String str = "";
			try{
				name = in.readUTF();
				clients.put(name, out);
				
				if(seat.size()==0){
					out.writeUTF("없음");
				}
				
				for(int i=0; i<seat.size(); i++){
					if(studentNum.get(i).equals(name)){
						Integer sn = new Integer(seat.get(i));
						int sni = sn.intValue()-1;
						out.writeUTF("같음"+sni);
					}else{
						Integer sn = new Integer(seat.get(i));
						int sni = sn.intValue()-1;
						out.writeUTF("다름"+sni);
					}
					if(i==(seat.size()-1)) out.writeUTF("끝");
				}
				while(in!=null){
					str =in.readUTF();
					if(!str.substring(0, 2).equals("퇴실")&&!str.substring(0, 2).equals("채팅")){
						seat.add(str.substring(2));
						studentNum.add(name);
						area.setText("");
						for(int i=0;i<seat.size(); i++){
							area.append(studentNum.get(i)+"\t"+seat.get(i)+"\n");
							sendToAll("입실"+seat.get(i));
						}
					}else if(str.substring(0, 2).equals("퇴실")){
						for(int i=0; i<seat.size(); i++){
							if(studentNum.get(i).equals(name)){
								sendToAll("퇴실"+seat.get(i));
								studentNum.remove(i);
								seat.remove(i);
							}
						}
						area.setText("");
						for(int i=0;i<seat.size(); i++){
							area.append(studentNum.get(i)+"\t"+seat.get(i)+"\n");
						}
						
					}
				}
			}catch(IOException e){}
			finally{
				clients.remove(name);
			}
		}
		void sendToAll(String msg){
			Iterator it = clients.keySet().iterator();
			while(it.hasNext()){
				try{
					DataOutputStream out = (DataOutputStream)clients.get(it.next());
					out.writeUTF(msg);
				}catch(IOException e){}
			}
		}
	}
}

