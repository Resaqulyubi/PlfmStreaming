package com.plfm.com.plfmstreaming.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.plfm.com.plfmstreaming.R;
import com.plfm.com.plfmstreaming.activities.MainActivity;
import com.plfm.com.plfmstreaming.etc.Const;
import com.plfm.com.plfmstreaming.etc.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.plfm.com.plfmstreaming.etc.Const.PREFS_RELOAD_CHAT;

public class ChatFragment extends Fragment  {


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);

        }
    }
    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;

    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    private Button sendButton;
    private ImageView imgvchange;
    private ListView lsvw_list;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText messageEditText;
    private ImageView mAddMessageImageView;
    ArrayAdapter<String> adapter;

    private final BroadcastReceiver reloadChat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(PREFS_RELOAD_CHAT)) {
                fetchData();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, container, false);
        messageEditText =view.findViewById(R.id.messageEditText);
        lsvw_list =view.findViewById(R.id.lsvw_list);
        sendButton =view.findViewById(R.id.sendButton);
        imgvchange =view.findViewById(R.id.imgvchange);
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



        if (Util.getSharedPreferenceString(getActivity() , Const.PREFS_NAMA, "").isEmpty()){
            dialogNama();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).chatSend(messageEditText.getText().toString());
                messageEditText.setText("");
            }
        });
        imgvchange.setOnClickListener(view1 -> {

            showDialogListOpsi(j -> {
                if (j == 0) {
                 dialogNama();
                }
            }, getActivity());

        } );


        return view;
    }

    public void showDialogListOpsi(ListenerDialog listener, Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context, R.style.Theme_Dialog_Margin_4);
        List<String> where = new ArrayList<String>();

        where.add("Edit nama");
      String[] strings = new String[where.size()];
        where.toArray(strings);

        builder.setItems(strings, (dialog, which) -> {
            switch (which) {
                case 0:
                    listener.onClick(0);
                    break;
                default:
                    listener.onClick(which);
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = Util.toDIP(context, 360);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        alertDialog.getWindow().setAttributes(lp);
    }


    private void dialogNama() {
        Dialog dialogAlternatif = new Dialog(getActivity(), R.style.Theme_Dialog_Fullscreen_Margin);
        dialogAlternatif.setContentView(R.layout.dialog_nama);

        Window window = dialogAlternatif.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialogAlternatif.setCancelable(false);
        dialogAlternatif.setCanceledOnTouchOutside(false);
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);


        final MaterialEditText met_nama =dialogAlternatif.findViewById(R.id.met_nama);
        ButtonRectangle btnRSimpan =dialogAlternatif.findViewById(R.id.btnRSimpan);

        if (!Util.getSharedPreferenceString(getActivity() , Const.PREFS_NAMA, "").isEmpty()){
            met_nama.setText(Util.getSharedPreferenceString(getActivity() , Const.PREFS_NAMA, ""));
        }

        btnRSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (met_nama.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    Util.setSharedPreference(getActivity(),Const.PREFS_NAMA,met_nama.getText().toString());
                    dialogAlternatif.dismiss();
                }

            }
        });


        dialogAlternatif.setOnDismissListener(dialog -> {
            keyboardhide();
        });


        dialogAlternatif.show();
        met_nama.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void keyboardhide(){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void enableSubmitIfReady() {

        boolean isReady = messageEditText.getText().toString().length() > 1;
        sendButton.setEnabled(isReady);
    }

    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).setOnBundleSelected(new MainActivity.SelectedBundle() {
            @Override
            public void onBundleSelect(Bundle bundle) {
                final String error = bundle.getString("error");
                if ( error!=null && !error.isEmpty()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                Log.d("aaaa", "onBundleSelect: "+bundle.toString());
            }
        });

        fetchData();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);


        }


    @Override
    public void onPause() {
        state = lsvw_list.onSaveInstanceState();

        super.onPause();
        getActivity().unregisterReceiver(reloadChat);


    }

    Parcelable state;



    @Override
    public void onResume() {
        super.onResume();
        IntentFilter reopenCashierFilter = new IntentFilter();
        reopenCashierFilter.addAction(PREFS_RELOAD_CHAT);
        getActivity().registerReceiver(reloadChat, reopenCashierFilter);
        fetchData();
    }

    private void fetchData(){
        List<String> strings =((MainActivity)getActivity()).getChat();
        if (strings!=null && strings.size()>0){

            if (adapter==null){
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, strings);
                lsvw_list.setAdapter(adapter);
            }else {
                if (strings.size()>adapter.getCount()    ){
                    adapter.addAll(strings.subList(adapter.getCount(),strings.size()-1));
                }
            }


            // Restore previous state (including selected item index and scroll position)
            if(state != null) {
                Log.d(TAG, "trying to restore listview state..");
                lsvw_list.onRestoreInstanceState(state);
            }
            adapter.notifyDataSetChanged();

//            lsvw_list.smoothScrollToPosition(adapter.getCount());
        }
    }




    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    public interface ListenerDialog {
        void onClick(int i);
    }


}
