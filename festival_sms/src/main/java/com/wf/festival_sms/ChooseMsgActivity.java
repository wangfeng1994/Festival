package com.wf.festival_sms;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wf.festival_sms.bean.FestivalLab;
import com.wf.festival_sms.bean.Msg;
import com.wf.festival_sms.fragment.FestivalCateoryFragment;

public class ChooseMsgActivity extends AppCompatActivity {

    private ListView lv;
    private FloatingActionButton fabToSend;
    private ArrayAdapter<Msg> mAdapter;
    private int festivalId;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_msg);
        festivalId = getIntent().getIntExtra(FestivalCateoryFragment.FESTIVAL_ID, -1);
        setTitle(FestivalLab.getInstance().getFestival(festivalId).getName());
        initViews();
        initEvent();
    }

    private void initEvent() {
        fabToSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMsgActivity.toActivity(ChooseMsgActivity.this,festivalId,-1);
            }
        });
    }

    private void initViews() {
        lv = (ListView) findViewById(R.id.list_view);
        fabToSend = (FloatingActionButton) findViewById(R.id.id_fab_send);
        inflater = LayoutInflater.from(this);
        lv.setAdapter(mAdapter = new ArrayAdapter<Msg>(this, -1, FestivalLab.getInstance().getMsgListByid(festivalId)) {
            @NonNull
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_msg, parent, false);
                }
                TextView content = (TextView) convertView.findViewById(R.id.tv_mag_content);
                Button toSend = (Button) convertView.findViewById(R.id.btn_to_send);
                content.setText("    "+getItem(position).getContent());
                toSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SendMsgActivity.toActivity(ChooseMsgActivity.this,festivalId,getItem(position).getId());
                    }
                });

                return convertView;
            }
        });
    }
}
