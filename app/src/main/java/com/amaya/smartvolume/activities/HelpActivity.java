package com.amaya.smartvolume.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amaya.smartvolume.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.amaya.smartvolume.activities.MainActivity.globalActivity;

public class HelpActivity extends AppCompatActivity {

    private LinearLayout ll_help_back;
    private Button btn_write_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        globalActivity = this;

        setUI();
        setListeners();
    }

    private void setUI() {
        ll_help_back = (LinearLayout) this.findViewById(R.id.ll_help_back);
        btn_write_message = (Button) this.findViewById(R.id.btn_write_message);
    }


    private void setListeners() {
        ll_help_back.setOnClickListener(new OnBackClickListener());
        btn_write_message.setOnClickListener(new OnWriteMessageButtonListener());
    }

    private class OnBackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            globalActivity.finish();
        }
    }

    private class OnWriteMessageButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            SimpleDateFormat now_formatter = new SimpleDateFormat("yyyy_MM_dd");
            String dateInfo = now_formatter.format(new Date());
            String subject = "SmartVolume - Sugerencia " + dateInfo;
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setData(Uri.parse("mailto:" + globalActivity.getString(R.string.email_receiver) + "?subject=" + subject));

            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT   , "");
            try {
                startActivity(Intent.createChooser(i, "Enviar sugerencia..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(globalActivity, "Es necesario disponer de un cliente de correo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
