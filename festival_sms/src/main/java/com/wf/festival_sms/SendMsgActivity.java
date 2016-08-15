package com.wf.festival_sms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wf.festival_sms.bean.Festival;
import com.wf.festival_sms.bean.FestivalLab;
import com.wf.festival_sms.bean.Msg;
import com.wf.festival_sms.biz.Sms;
import com.wf.festival_sms.view.FlowLayout;

import java.util.HashSet;

/**
 * Created by lenovo on 2016/8/14.
 */

public class SendMsgActivity extends AppCompatActivity {
    private static final int CODE_REQUEST = 1;
    private static final String TAG = SendMsgActivity.class.getSimpleName();
    private int festivalId;
    private int msgId;
    public static final String FESTIVAL_ID = "festivalId";
    public static final String MSG_ID = "msgId";
    private Festival mFestival;
    private Msg mMsg;
    private EditText etMsg;
    private Button btnAdd;
    private FlowLayout flContacts;
    private FloatingActionButton fabSend;
    private View loading;
    private HashSet<String> setName = new HashSet<String>();
    private HashSet<String> setNum = new HashSet<String>();
    private LayoutInflater inflater;
    public static final String ACTION_SEND_MSG = "action_send_msg";
    public static final String ACTION_DELIVER_MSG = "action_deliver_msg";
    private PendingIntent sendIntent;
    private PendingIntent deliverIntent;
    private BroadcastReceiver sendReceiver;
    private BroadcastReceiver deliverReceiver;
    private Sms sms = new Sms();
    private int msgCount;
    private int totalCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);
        inflater = LayoutInflater.from(this);
        initDatas();
        initViews();
        initEvents();
        initRece();
    }

    private void initRece() {
        Intent intent1 = new Intent(ACTION_SEND_MSG);
        Intent intent2 = new Intent(ACTION_SEND_MSG);
        sendIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        deliverIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
        registerReceiver(sendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                msgCount++;
                if (getResultCode() == RESULT_OK) {
                    Log.i(TAG, "onReceive:success ");
                } else {
                    Log.i(TAG, "onReceive: error");
                }
                Toast.makeText(getApplicationContext(), msgCount + "/" + totalCount, Toast.LENGTH_SHORT).show();
                if (msgCount == totalCount) {
                    finish();
                }
            }
        }, new IntentFilter(ACTION_SEND_MSG));
        registerReceiver(deliverReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == RESULT_OK) {
                    Log.i(TAG, "onReceive: receive");
                }
            }
        }, new IntentFilter(ACTION_DELIVER_MSG));
    }

    private void initEvents() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CODE_REQUEST);
            }
        });
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setNum.size() == 0) {
                    Toast.makeText(SendMsgActivity.this, "choose", Toast.LENGTH_SHORT).show();
                }
                String content = etMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    loading.setVisibility(View.VISIBLE);
                    totalCount = sms.sendMsg(setNum, content, sendIntent, deliverIntent);

                }
            }
        });

    }

    private void initViews() {
        etMsg = (EditText) findViewById(R.id.et_msg_content);
        btnAdd = (Button) findViewById(R.id.btn_add);
        flContacts = (FlowLayout) findViewById(R.id.fl_layout);
        fabSend = (FloatingActionButton) findViewById(R.id.fab_send);
        loading = findViewById(R.id.load_layout);
        loading.setVisibility(View.GONE);

        if (msgId != -1) {
            mMsg = FestivalLab.getInstance().getMsgById(msgId);
            etMsg.setText(mMsg.getContent());
        }
    }

    private void initDatas() {
        festivalId = getIntent().getIntExtra(FESTIVAL_ID, -1);
        msgId = getIntent().getIntExtra(MSG_ID, -1);
        mFestival = FestivalLab.getInstance().getFestival(festivalId);
        setTitle(mFestival.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contact = data.getData();
                Cursor cursor = getContentResolver().query(contact, null, null, null, null);
                cursor.moveToFirst();
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = getContactNumber(cursor);
                if (!TextUtils.isEmpty(number)) {
                    setNum.add(number);
                    setName.add(contactName);
                    addTag(contactName);
                }
            }
        }
    }

    private void addTag(String contactName) {
        TextView tv = (TextView) inflater.inflate(R.layout.tag, flContacts, false);
        tv.setText(contactName);
        flContacts.addView(tv);
    }

    private String getContactNumber(Cursor cursor) {
        int numberCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        String number = null;
        if (numberCount > 0) {
            int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            phoneCursor.moveToFirst();
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneCursor.close();
        }
        cursor.close();
        return number;
    }

    public static void toActivity(Context context, int festivalId, int msgId) {
        Intent intent = new Intent(context, SendMsgActivity.class);
        intent.putExtra(FESTIVAL_ID, festivalId);
        intent.putExtra(MSG_ID, msgId);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sendReceiver);
        unregisterReceiver(deliverReceiver);
    }
}
