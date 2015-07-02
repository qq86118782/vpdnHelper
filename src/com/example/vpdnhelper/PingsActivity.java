package com.example.vpdnhelper;

import java.util.Iterator;
import java.util.List;

import util.CommandUtil;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PingsActivity extends Activity {
	private Button pingBtn;
	private EditText descText;
	private TextView pingView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pings);
		pingBtn=(Button)this.findViewById(R.id.pingItBtn);
	    descText=(EditText)this.findViewById(R.id.dest);
	    pingView=(TextView)this.findViewById(R.id.pingView);
		
	    pingBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View e) {// ���������� com.jshx.pinganyun
				// Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
				// startActivity(intent);
				if(descText.getText()!=null){
				CommandTask sTask;
				try {
					sTask = new CommandTask("ping -c 1  "+descText.getText().toString());
					sTask.execute();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// tv.setText(CommandUtil.execute("ping 58.213.147.1 -c 1").get(0));
				// System.err.println("test:"+Network.getPingStats("218.2.135.1"));
			}else{
				Toast.makeText(getApplicationContext(), "������Ŀ���ַ����������", Toast.LENGTH_SHORT)
				.show();
			}
			}
		});
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pings, menu);
		return true;
	}

	
	class CommandTask extends AsyncTask<Integer, Integer, String> {
		// ����������ڷֱ��ǲ��������������߳���Ϣʱ�䣩������(publishProgress�õ�)������ֵ ����

		String dest;
		String returned="";

		public CommandTask(String destn) {

			dest = destn;
		}

		public CommandTask() {

			dest = "";
		}

		@Override
		protected void onPreExecute() {
			// ��һ��ִ�з���
		Toast.makeText(getApplicationContext(), "��ʼִ�У� "+dest+"!", Toast.LENGTH_SHORT)
					.show();
		//	print_btn.setEnabled(false);
		//	print_btn.setText("���ڴ�ӡ");
		//	pb1.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {
//			try {
//
//				Runtime run = Runtime.getRuntime();
//				Process proc = null;
//				try {
//					String str = "ping -c 1 -i 0.2 -W 1 " + dest;
//
//					proc = run.exec(str);
//					int result = proc.waitFor();
//					if (result == 0) {
//						returned= true;
//					} else {
//						returned= false;
//					}
//				} catch (Exception ef) {
//					ef.printStackTrace();
//				}
//
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
			
			List<String> list=CommandUtil.execute(dest);
			if(list!=null){
				Iterator<String> it=list.iterator();
				while(it.hasNext()){
					returned+=it.next()+"\n";
				}
			}
			return "";
	
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// ���������doInBackground����publishProgressʱ��������Ȼ����ʱֻ��һ������
			// ��������ȡ������һ������,����Ҫ��progesss[0]��ȡֵ
			// ��n����������progress[n]��ȡֵ
			super.onProgressUpdate(progress);
		}

		@Override
		protected void onPostExecute(String r) {
			// doInBackground����ʱ���������仰˵������doInBackgroundִ����󴥷�
			// �����result��������doInBackgroundִ�к�ķ���ֵ������������"ִ�����"
			// setTitle(result);
			pingView.setText("���Խ����\n"+returned);
		 
			Toast.makeText(getApplicationContext(), "ִ�� ���!", Toast.LENGTH_SHORT)
					.show();
			super.onPostExecute(r);
		}

	}
}
