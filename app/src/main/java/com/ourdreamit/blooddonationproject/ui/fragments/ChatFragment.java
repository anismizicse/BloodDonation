package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.core.chat.ChatContract;
import com.ourdreamit.blooddonationproject.core.chat.ChatPresenter;
import com.ourdreamit.blooddonationproject.events.PushNotificationEvent;
import com.ourdreamit.blooddonationproject.models.Chat;
import com.ourdreamit.blooddonationproject.ui.adapters.ChatRecyclerAdapter;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


public class ChatFragment extends Fragment implements ChatContract.View, TextView.OnEditorActionListener {
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;
    FloatingActionButton fab_send_msg;
    String userType;
    int recentChat;

    public static ChatFragment newInstance(String receiver,
                                           String receiverUid,
                                           String firebaseToken) {
        //Log.d("ChatTrace", "ChatFragment newInstance");
        Bundle args = new Bundle();
        args.putString(Constants.ARG_RECEIVER, receiver);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d("ChatTrace", "ChatFragment onStart");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d("ChatTrace", "ChatFragment onStop");
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d("ChatTrace", "ChatFragment onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(fragmentView);
        Log.d("Finaltrace","ChatFragment onCreateView Called"+ DataLoader.doctorCarePanel+" "+
        DataLoader.donorToDoctorChat+" "+
        DataLoader.doctorToDonorChat);
        userType = new SharedPrefUtil(getActivity()).getString(DataLoader.USERTYPE, "na");


        //ChatRecyclerAdapter.context = getActivity();

        return fragmentView;
    }

    private void bindViews(View view) {
        //Log.d("ChatTrace", "ChatFragment bindViews");
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) view.findViewById(R.id.edit_text_message);
        fab_send_msg = (FloatingActionButton) view.findViewById(R.id.fab_send_msg);

        fab_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(DataLoader.buttonClick);
                if (!mETxtMessage.getText().toString().isEmpty())
                    sendMessage();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d("ChatTrace", "ChatFragment onActivityCreated");
        init();
    }

    private void init() {
        //Log.d("ChatTrace", "ChatFragment init");
        mProgressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mETxtMessage.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getArguments().getString(Constants.ARG_RECEIVER_UID));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //Log.d("ChatTrace", "ChatFragment onEditorAction");
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            if (!mETxtMessage.getText().toString().isEmpty())
                sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        recentChat = new SharedPrefUtil(getActivity()).getInt(DataLoader.RECENTCHAT);

        if (userType.equals("donor") && !userType.equals("na") && recentChat == 0 && !DataLoader.doctorCarePanel) {

            if(DataLoader.profileInfo.phone != null && DataLoader.adminInfo.phone != null) {
                DataLoader.updateAdminChatList(DataLoader.profileInfo.phone, DataLoader.adminInfo.phone);
                new SharedPrefUtil(getActivity()).saveInt(DataLoader.RECENTCHAT, 1);
            }

        }else if (userType.equals("donor") && !userType.equals("na") && recentChat == 0
                && DataLoader.doctorCarePanel && DataLoader.donorToDoctorChat) {

            if(DataLoader.profileInfo.phone != null && DataLoader.doctorPhone != null) {
                DataLoader.updateDoctorChatList(DataLoader.profileInfo.phone, DataLoader.doctorPhone);
                new SharedPrefUtil(getActivity()).saveInt(DataLoader.RECENTCHAT, 1);
            }

        }

        //Log.d("ChatTrace", "ChatFragment sendMessage");
        String message = mETxtMessage.getText().toString();
        String receiver = getArguments().getString(Constants.ARG_RECEIVER);
        String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
//        Log.d("userDetails","sender: "+sender
//                +" receiver: "+receiver
//                +" senderUid: "+senderUid
//                +" receiverUid: "+receiverUid
//                +" message: "+message
//                +" Time: "+System.currentTimeMillis()
//
//        );
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(getActivity().getApplicationContext(),
                chat,
                receiverFirebaseToken);
    }

    @Override
    public void onSendMessageSuccess() {
        //Log.d("ChatTrace", "ChatFragment onSendMessageSuccess");
        mETxtMessage.setText("");
        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        //Log.d("ChatTrace", "ChatFragment onSendMessageFailure");
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        //Log.d("ChatTrace", "ChatFragment onGetMessagesSuccess");
        if (mChatRecyclerAdapter == null) {
            //Log.d("ChatTrace", "ChatFragment onGetMessagesSuccess mChatRecyclerAdapter null");
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        // Log.d("ChatTrace","ChatFragment onGetMessagesFailure");
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        //Log.d("ChatTrace", "ChatFragment onPushNotificationEvent");
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid());
        }
    }
}
