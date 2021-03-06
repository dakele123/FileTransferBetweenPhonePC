package com.example.practice.filetransferbetweenphonepc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class MainActivity extends AppCompatActivity {
    private Button FSendButton;
    private Button FReceiveButton;
    private EditText ipet;
    private String ipstr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置View为activity_main
        setContentView(R.layout.activity_main);
        //设置ipet与edittext相关联
        ipet=(EditText)findViewById(R.id.ipet);
        //此处为服务器ip
        ipet.setText("10.108.169.59");
        //--------------文件接收到手机-------------------//
        FReceiveButton=(Button)findViewById(R.id.FReceive);
        //添加receiveButton单击监听
        FReceiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将ipstr设置为服务器ip
                ipstr=ipet.getText().toString();
                //新线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketChannel socketChannel=null;
                        try{
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            SocketAddress socketAddress = new InetSocketAddress(ipstr, 1991);
                            socketChannel.connect(socketAddress);
                            //手机接收文件，存储位置为data/data/com.example.practice.filetransferbetweenphonepc，
                            //文件名为123.txt
                            receiveFile(socketChannel, new File("data/data/com.example.practice.filetransferbetweenphonepc/456.txt"));
                        }catch (Exception ex){
                            Log.i("FReceiveERROR",null,ex);
                        }
                    }
                }).start();
            }
        });
        //------------------文件发送到电脑---------------------//
        FSendButton=(Button)findViewById(R.id.FSend);
        //添加sendButton单击监听
        FSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将ipstr设置为服务器ip
                ipstr=ipet.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketChannel socketChannel=null;
                        try {
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            SocketAddress socketAddress = new InetSocketAddress(ipstr, 1991);
                            socketChannel.connect(socketAddress);
                            //手机发送文件，文件位置为data/data/com.example.practice.filetransferbetweenphonepc，
                            //文件名为123.txt
                            sendFile(socketChannel, new File("data/data/com.example.practice.filetransferbetweenphonepc/123.txt"));
                        }catch (Exception ex){
                            Log.i("FSendERROR",null,ex);
                        }
                    }
                }).start();
            }
        });

    }

    private static void sendFile(SocketChannel socketChannel, File file) throws IOException {
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int size = 0;
            while ((size = channel.read(buffer)) != -1) {
                buffer.rewind();
                buffer.limit(size);
                socketChannel.write(buffer);
                buffer.clear();
            }
            socketChannel.socket().shutdownOutput();
        } finally {
            try {
                channel.close();
            } catch(Exception ex) {}
            try {
                fis.close();
            } catch(Exception ex) {}
        }
    }

    private static void receiveFile(SocketChannel socketChannel, File file) throws IOException {
        FileOutputStream fos = null;
        FileChannel channel = null;

        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

            int size = 0;
            while ((size = socketChannel.read(buffer)) != -1) {
                buffer.flip();
                if (size > 0) {
                    buffer.limit(size);
                    channel.write(buffer);
                    buffer.clear();
                }
            }
        } finally {
            try {
                channel.close();
            } catch(Exception ex) {}
            try {
                fos.close();
            } catch(Exception ex) {}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
