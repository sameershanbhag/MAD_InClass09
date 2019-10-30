package com.example.inclass09;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class InboxFragment extends Fragment implements InboxRecyclerView.DeleteListner {

    private OnFragmentInteractionListener mListener;

    RecyclerView.Adapter mAdapter;

    Messages currentMessage;

    private String DELETE_URL = "http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/";

    private final String INBOX_URL = "http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox";

    private final OkHttpClient client = new OkHttpClient();

    private ImageView iv_logout;
    private ImageView iv_cmail;
    private RecyclerView lv_mlist;
    private TextView tv_username;

    private ArrayList<Messages> messageList = new ArrayList<>();

    private String token;
    String userName;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mPrefs = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        token = mPrefs.getString("userToken","");
        userName = mPrefs.getString("userFname", "") + " " + mPrefs.getString("userLname", "");

        getInbox(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = mHandler.obtainMessage(400, "Error");
                message.sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Messages temp = new Messages();
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray messages = root.getJSONArray("messages");
                    for(int i = 0;i < messages.length(); i++){
                        JSONObject mes = messages.getJSONObject(i);
                        temp.setMessage(mes.getString("message"));
                        temp.setSenderFname(mes.getString("sender_fname"));
                        temp.setSenderLname(mes.getString("sender_lname"));
                        temp.setSubject(mes.getString("subject"));
                        temp.setSenderId(mes.getString("sender_id"));
                        temp.setReceiverId(mes.getString("receiver_id"));
                        temp.setCreatedAt(mes.getString("created_at"));
                        temp.setUpdatedAt(mes.getString("updated_at"));
                        temp.setID(mes.getString("id"));
                        messageList.add(temp);
                    }
                    Message message = mHandler.obtainMessage(250, "success");
                    message.sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(getActivity()).setTitle("Inbox");

        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        iv_logout = view.findViewById(R.id.iv_logout);
        iv_cmail= view.findViewById(R.id.iv_cmail);
        lv_mlist = view.findViewById(R.id.lv_mlist);
        tv_username = view.findViewById(R.id.tv_username);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_username.setText(userName);



        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mPrefs = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
                SharedPreferences.Editor mPrefEdit = mPrefs.edit();
                mPrefEdit.remove("userFname");
                mPrefEdit.remove("userLname");
                mPrefEdit.remove("userToken");
                mPrefEdit.apply();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MailerFragment(), "mailerFragment")
                        .commit();
            }
        });

        iv_cmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.composeMailFragment();
            }
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lv_mlist.setLayoutManager(layoutManager);
        mAdapter = new InboxRecyclerView(messageList, this);
        lv_mlist.setAdapter(mAdapter);
    }

    public interface OnFragmentInteractionListener {
        void composeMailFragment();
        void viewMail(Messages currentMessage);
    }

    Call getInbox(Callback callback) {
        Request request = new Request.Builder()
                .url(INBOX_URL)
                .addHeader("Authorization", "BEARER " + token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    Call delInbox(Callback callback, String delId) {
        Request request = new Request.Builder()
                .url(DELETE_URL + delId)
                .addHeader("Authorization", "BEARER " + token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    @Override
    public void deleteMethod(final String delId) {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        delInbox(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Message message = mHandler.obtainMessage(200, "Deletion Failed");
                                message.sendToTarget();
                            }

                            @Override
                            public void onResponse(Call call, Response response) {
                                Message message = mHandler.obtainMessage(200, "success");
                                message.sendToTarget();
                            }
                        }, delId);
                    }
                });
            }
        }.start();
    }

    @Override
    public void displayMethod(Messages messages) {
        mListener.viewMail(messages);
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if(message.what == 200){
                Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
            } else if (message.what == 400){
                Toast.makeText(getActivity(), "Error Retrieving Messages", Toast.LENGTH_SHORT).show();
            } else if(message.what == 250){
                mAdapter.notifyDataSetChanged();
            } else if(message.what == 350){
                Toast.makeText(getActivity(), "Error Deleting the Message", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
