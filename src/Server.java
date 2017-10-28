import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
	int i=0;
	HashMap<String, DataOutputStream> clients;
	ArrayList<String> daehwa = new ArrayList<String>();
	Server(){
		clients = new HashMap<String, DataOutputStream>();
		Collections.synchronizedMap(clients);
	}
	public void start(){
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(8522);
			System.out.println("¼­¹ö°¡ ½ÃÀÛµÇ¾ú½À´Ï´Ù.");
			while(true){
				socket =serverSocket.accept();
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
				}
			}catch(Exception e){
				e.printStackTrace();
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
	public static void main(String[] args){
		new Server().start();
		
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
				if(name.equals("¼Õ´Ô")){
					i++;
					name=i+"¹ø "+name.substring(0, 1);
				}
				
				sendToAll("#"+name+"´ÔÀÌ µé¾î¿À¼Ì½À´Ï´Ù.");
				clients.put(name, out);
				if(daehwa.size()==0){
					out.writeUTF("¾øÀ½");
				}
				for(int i=0; i<daehwa.size(); i++){
					out.writeUTF(daehwa.get(i));
					if(i==(daehwa.size()-1)) out.writeUTF("³¡");
				}
				while(in!=null){
						str =in.readUTF();
						sendToAll(str.substring(0, 2).equals("¼Õ´Ô")?(name+"´Ô"+str.substring(3)):str);
						if(daehwa.size()!=10){
							daehwa.add(str.substring(0, 2).equals("¼Õ´Ô")?(name+"´Ô"+str.substring(3)):str);
						}else{
							daehwa.remove(0);
							daehwa.add(str.substring(0, 2).equals("¼Õ´Ô")?(name+"´Ô"+str.substring(3)):str);
						}
						if(!str.substring(0, 2).equals("¼Õ´Ô")){
							name = str.substring(0, str.indexOf(" :"));
						}
				}

			}catch(IOException e){
				
			}finally{
				sendToAll("#"+name+"´ÔÀÌ ³ª°¡¼Ì½À´Ï´Ù.");
				clients.remove(name);
				
			}
			
		}
	}
}
