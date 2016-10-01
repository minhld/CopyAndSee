package com.minhld.copynsee.ui;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.minhld.copynsee.R;
import com.minhld.copynsee.business.DataDownloader;
import com.minhld.copynsee.business.UIProvider;
import com.minhld.copynsee.components.CircularProgressBar;
import com.minhld.copynsee.data.DataProvider;
import com.minhld.copynsee.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.loadProg)
    CircularProgressBar loadProg;
    @BindView(R.id.statusTxt)
    TextView statusTxt;

    boolean isAppExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        // get configuration and application availability
        Utils.initUtils(IntroActivity.this);

        // prepare for data downloading.
        DataDownloader downloader = new DataDownloader(IntroActivity.this);
        downloader.setBookDownloadListener(new DataDownloader.BookDownloadListener() {
            @Override
            public void update(int code, int progress) {
                switch (code){
                    case DataDownloader.STATUS_STARTING : {
                        loadProg.setVisibility(View.VISIBLE);
                        loadProg.setProgress(0);
                        loadProg.setTitle("0%");
                        loadProg.setSubTitle(getString(R.string.text_download_data));
                        break;
                    }
                    case DataDownloader.STATUS_ONGOING : {
                        loadProg.setProgress(progress);
                        loadProg.setTitle(progress + "%");
                        loadProg.setSubTitle(getString(R.string.text_download_data));
                        break;
                    }
                    case DataDownloader.STATUS_UNZIP_FILE : {
                        loadProg.setSubTitle(getString(R.string.text_download_unzip));
                        break;
                    }
                    case DataDownloader.STATUS_FINISHED : {
                        loadProg.setVisibility(View.GONE);
                        startupService(0);
                        break;
                    }
                    case DataDownloader.STATUS_FILE_EXISTED : {
                        loadProg.setVisibility(View.GONE);
                        startupService(1000);
                        break;
                    }
                    default : {
                        loadProg.setVisibility(View.GONE);
                        statusTxt.setVisibility(View.VISIBLE);
                        Utils.vibrate(IntroActivity.this, 500);
                        startupService(2000, true);
                        break;
                    }
                }
            }
        });
        downloader.execute();
    }

    void startupService(int runAfterMillisecond, final boolean... isExit){
        new Handler().postDelayed(new Thread(){
            public void run(){
                if (isExit.length > 0 && isExit[0]){
                    // DB files are failed to download
                    System.exit(0);
                    return;
                }else{
                    // start floating service
                    UIProvider.initiateFloatingService(IntroActivity.this);

                    // remove the current introduction window
                    Utils.toast(IntroActivity.this, R.string.ui_notice_run_in_bg, 3000);

                    // loading DB
                    DataProvider.openDbSync(IntroActivity.this);

                    // close the intro activity
                    finish();
                }
            }
        }, runAfterMillisecond);
    }
}
