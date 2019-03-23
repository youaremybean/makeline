//可以参考下，这是工作需求已经实现的，大概就是读不同文件夹的txt文本，换行符用作区分不同行数据的表示，将公交线路的上下行站点信息通过
//一定的协议要求组合成bin文件，烧录到报站器，让其发出

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;

public class GenericRoad {
	static String rootpath="D:/生成线路/line.bin";
	static String path="D:/生成线路/编号";
	static String upload="/上行";
	static String download="/下行";
	static String lineup="/上行线路名";
	static String linedown="/下行线路名";
	static String tail=".txt";
	static int sumline=0;
	Integer[] head = new Integer[]{0x55,0xAA,0x6B,0x00,0x00};
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.println("先按指定规则做好线路文件再输入要生成的线路数目（输入完成enter结束）：");
		sumline = s.nextInt();
		System.out.println("要生成的线路数目为:"+sumline);		
		GenericRoad g1 = new GenericRoad();
		GenericRoad g2 = new GenericRoad();
		for(int i=1;i<=sumline;i++){
			g2.combine(path+new String().valueOf(i),upload,g1.generic(path+new String().valueOf(i),lineup,upload));
			g2.combine(path+new String().valueOf(i),download,g1.generic(path+new String().valueOf(i),linedown,download));
			g2.union(path+new String().valueOf(i));
		}
		g2.union(sumline);
		System.out.println("文件生成成功！");
	}
	public void union(int num){
		DataInputStream dis=null;
		DataOutputStream dos=null;
		try{
			dos= new DataOutputStream(new FileOutputStream(rootpath));
			for(int i=1;i<=num;i++){
				File f = new File(path+new String().valueOf(i));
				String str1= f.getAbsolutePath().toString()+"/"+f.getName()+".bin";
				dis = new DataInputStream(new FileInputStream(str1));
				int len ;
				byte[] b = new byte[64];
				while((len=dis.read(b))!=-1)
					dos.write(b, 0, len);
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
					if(dis!=null){
						try {
							dis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(dos!=null){
						try {
							dos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
		}		
	}
	public void union(String src){
		File f = new File(src);
		String str1 =f.getAbsolutePath().toString()+upload+".bin";
		String str2 =f.getAbsolutePath().toString()+download+".bin";
		String str3 =f.getAbsolutePath().toString()+"/"+f.getName()+".bin";
		DataInputStream dis1=null;
		DataInputStream dis2=null;
		DataOutputStream dos=null;
		try {
			dis1 = new DataInputStream(new FileInputStream(str1));
			dis2 = new DataInputStream(new FileInputStream(str2));
			dos= new DataOutputStream(new FileOutputStream(str3));
			int len ;
			byte[] b = new byte[64];
			while((len=dis1.read(b))!=-1){
				dos.write(b, 0, len);
			}
			while((len=dis2.read(b))!=-1){
				dos.write(b, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(dis1!=null){
				try {
					dis1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(dis2!=null){
				try {
					dis2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(dos!=null){
				try {
					dos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	public void combine(String src,String downorup,int lengh){
		File f = new File(src);
		String str1 =f.getAbsolutePath().toString()+downorup+".bin";
		byte xor=0;
		byte[] b2 = getlen(lengh+2);
		byte[] b3 = new byte[lengh];
		byte[] b4 = new byte[2048-2-lengh];
		Arrays.fill(b4, new Integer(0xff).byteValue());
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(str1,"rw");
			raf.seek(3);
			raf.write(b2);
			raf.seek(0);
			raf.read(b3);
			for(int i=0;i<lengh;i++){
				xor ^=b3[i];
			}
			raf.seek(lengh);
			b2[0]=xor;
			b2[1]=new Integer(0xff).byteValue();
			raf.write(b2);
			raf.write(b4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(raf!=null){
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	public int generic(String src,String desc,String downorup){
			File f = new File(src);
			//线路名路径
			String str1=f.getAbsolutePath().toString()+desc+tail;
			//线路内容路径
			String str2=f.getAbsolutePath().toString()+downorup+tail;
			//生成bin文件
			String str3=f.getAbsolutePath().toString()+downorup+".bin";
			BufferedReader  dis1=null;
			BufferedReader  dis2=null;
			DataOutputStream dos=null;
			int lengh =0;
			byte num=1;
			byte temp=0;
			try {
				dis1 = new BufferedReader(new FileReader(str1));
				dis2 = new BufferedReader(new FileReader(str2));
				dos = new DataOutputStream(new FileOutputStream(str3));	
				//dis3 = new DataInputStream(new FileInputStream(f.getAbsolutePath().toString()+downorup+".bin"));
				String s;
				byte[] b;
				for(int k =0;k<5;k++)
				dos.write(head[k].byteValue());					
				lengh=5;
				while((s=dis1.readLine())!=null){
					b=s.getBytes();
					byte len =(byte)b.length;
					lengh+=(len+1);
					dos.write(len);	
					dos.write(b);
					dos.flush();					
				}
				if(downorup.equals(upload)){
					temp=1;
				}else if(downorup.equals(download)){
					temp=2;
				}
				dos.write(temp);
				lengh=lengh+1;
				while((s=dis2.readLine())!=null){
					b=s.getBytes();
					byte len =(byte)b.length;
					lengh+=(len+2);
					dos.write(num++);	
					dos.write(len);	
					dos.write(b);
					dos.flush();					
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();		
			}finally{
				if(dis1!=null){					
					try {
						dis1.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(dis2!=null){					
					try {
						dis2.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(dos!=null){					
					try {
						dos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
			}
			return lengh;
	}
	public byte[] getlen(int lengh){
		//可用正数范围为0到127
		int[] s  = new int[2];
		s[0] = lengh/256;
		s[1] = lengh%256;	
		byte[] twob = new byte[2];
		for(int i=0;i<2;i++){
			if(s[i]<128){
				twob[i]=(byte)s[i];
			}else if((s[i]>=128)&&(s[i]<=255)){
				twob[i]=(byte)(s[i]-256);
			}
		}
		return twob;
	}
}
