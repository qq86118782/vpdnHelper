package util;


public class HttpHelper {
	
	//����ǰ��Ҫ�޸Ĵ˴���androidManifest�а汾��SERVER�е�һ��
	private static boolean REALENVIROMENT=true;
	
	public static String getServerPath(){
		if(REALENVIROMENT){
			//��ʵ����ʹ����������
			return "http://218.2.132.16/vpdn/";
		}else{
		//132.229.148.81 �ҵĵ���
		return "http://192.168.1.15:8080/grid/";
		}
		
	}
	
	
}
