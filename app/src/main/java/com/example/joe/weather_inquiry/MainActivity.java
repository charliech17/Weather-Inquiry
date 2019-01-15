package com.example.joe.weather_inquiry;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btn1;
    TextView tx1,tx2,tx3,tx4;
    public HttpURLConnection http;
    String webgetdata="https://thingspeak.com/channels/672615/feeds/last.json?key=K189IXLOP1G254OV&timezone=Asia%2FTaipei",str1,str2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();

        btn1Click();

    }

    private void btn1Click() {
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"查詢中...請稍後...",Toast.LENGTH_SHORT).show();
                Thread t1=new MyThread1();
                t1.start();
            }
        });

    }

    private void findViewById() {
    btn1=(Button)findViewById(R.id.button);
    tx1=(TextView)findViewById(R.id.textView7);
    tx2=(TextView)findViewById(R.id.textView5);
    tx3=(TextView)findViewById(R.id.textView6);
    tx4=(TextView)findViewById(R.id.textView9);

    tx1.setText("                                             按下 '查詢' 可以取得最新雲端天氣資訊");
    btn1.setBackgroundColor(Color.BLUE);
    }

    private class MyThread1 extends Thread {
        public void run(){
            try {
                URL u1=new URL(webgetdata);
                http=(HttpURLConnection)u1.openConnection();
                http.setRequestMethod("GET");

                InputStream input=http.getInputStream();
                byte[] data=new byte[1024];
                int idx=input.read(data);
                String str=new String(data,0,idx);

                JSONObject jsonObject = new JSONObject(str);
                String creatTime=jsonObject.getString("created_at");
                String field1=jsonObject.getString("field1");
                String field2=jsonObject.getString("field2");
                String[] obj={creatTime,field1,field2};

                Message[] msg=new Message[3];
                for(int i=1;i<=3;i++){
                    msg[i-1]=handler.obtainMessage(i,obj[i-1]);
                    handler.sendMessage(msg[i-1]);
                }

                sleep(200);

                input.close();
                http.disconnect();

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String MsgString = (String)msg.obj;
            switch(msg.what){
                case 1:
                    str1="*日期: "+MsgString.substring(0,10);
                    str2="\n*時間: "+MsgString.substring(11,16);
                    tx4.setText(str1+str2);
                    break;
                case 2:
                    tx2.setText(MsgString+"°C");
                    break;

                case 3:
                    tx3.setText(MsgString+"%");
                    break;
            }


            return false;
        }
    });


}
