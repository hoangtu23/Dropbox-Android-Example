package net.asplode.dropboxtest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class Main extends Activity {
    private DropboxAPI<AndroidAuthSession> mDBApi;
    final static String APP_KEY = "YOUR APP KEY HERE";
    final static String APP_SECRET = "YOUR SECRET KEY HERE";
    final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button link = (Button) findViewById(R.id.button1);
        Button upload = (Button) findViewById(R.id.button2);
        Button download = (Button) findViewById(R.id.button3);

        link.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
                AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                mDBApi.getSession().startAuthentication(Main.this);
            }
        });
        upload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(getFilesDir().getAbsolutePath());
                try {
                    PrintWriter out = new PrintWriter(new FileWriter(dir + "/test.txt"));
                    for (int i = 0; i < 20; i++) {
                        out.println("omg");
                    }
                    out.close();
                    File file = new File(getFilesDir().getAbsolutePath(), "/test.txt");
                    FileInputStream in = new FileInputStream(file);
                    mDBApi.putFileOverwrite("/test.txt", in, file.length(), null);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DropboxException e) {
                    e.printStackTrace();
                }

            }
        });
        download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File output = new File("/mnt/sdcard/test.txt");
                    OutputStream out = new FileOutputStream(output);
                    mDBApi.getFile("/test.txt", null, out, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DropboxException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDBApi != null && mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();
                AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
                // Something something save tokens.
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }
}