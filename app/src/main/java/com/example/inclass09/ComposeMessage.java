package com.example.inclass09;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


public class ComposeMessage extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<Users> UserList = new ArrayList<>();
    ArrayAdapter<Users> dataAdapter;

    private Users selectedUser = null;

    private String token;

    private String USERS = "http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users";

    private String POST_MSG = "http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add";

    private final OkHttpClient client = new OkHttpClient();

    private Spinner sp_emailList;

    private EditText et_message;
    private EditText et_sub;
    private Button bt_sendmessage;
    private Button bt_cancle;

    public ComposeMessage() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mPrefs = Objects.requireNonNull(getActivity()).getSharedPreferences("USER", MODE_PRIVATE);
        token = mPrefs.getString("userToken","");

        getUsers(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = mHandler.obtainMessage(150, "Error");
                message.sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray users = root.getJSONArray("users");
                    for(int i = 0;i < users.length(); i++){
                        Users temp = new Users();
                        JSONObject user = users.getJSONObject(i);
                        temp.setFirstName(user.getString("fname"));
                        temp.setLastName(user.getString("lname"));
                        temp.setId(user.getString("id"));
                        UserList.add(temp);
                    }
                    Message message = mHandler.obtainMessage(200, "success");
                    message.sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, UserList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp_emailList.setAdapter(dataAdapter);

        sp_emailList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = UserList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), "Please Select A Receiver", Toast.LENGTH_SHORT).show();
            }
        });

        // Start With the Business Logic

        bt_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_message.getText().toString().equals("") || et_message.getText().toString().trim().length() == 0){
                    et_message.setError("Please Enter a Valid Message");
                } else if(selectedUser == null) {
                    Toast.makeText(getActivity(), "Please Select a Receiver", Toast.LENGTH_SHORT).show();
                } else if(et_sub.getText().toString() == "" || et_sub.getText().toString().trim().length() == 0){
                    et_sub.setError("Please Enter a Valid Subject");
                } else {
                    sendMessage(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message message = mHandler.obtainMessage(450, "fail"); // change 450 with 350 to show error
                            message.sendToTarget();
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            Message message = mHandler.obtainMessage(450, "success");
                            message.sendToTarget();
                        }
                    });
                }
            }
        });

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.composeMessage();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Create New Email");

        View view = inflater.inflate(R.layout.fragment_compose_message, container, false);

        sp_emailList = view.findViewById(R.id.sp_emailList);
        et_message = view.findViewById(R.id.et_message);
        et_sub = view.findViewById(R.id.et_sub);
        bt_sendmessage = view.findViewById(R.id.bt_sendmessage);
        bt_cancle = view.findViewById(R.id.bt_cancle);

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

    public interface OnFragmentInteractionListener {
        void composeMessage();
    }


    Call getUsers(Callback callback) {
        Request request = new Request.Builder()
                .url(USERS)
                .addHeader("Authorization", "BEARER " + token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    Call sendMessage(Callback callback) {
        RequestBody formBody = new FormBody.Builder()
                .add("receiver_id", selectedUser.getId())
                .add("subject", et_sub.getText().toString())
                .add("message", et_message.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url(POST_MSG)
                .addHeader("Authorization", "BEARER " + token)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            // This is where you do your work in the UI thread.
            // Your worker tells you in the message what to do.
            if(message.what == 200){
                Toast.makeText(getActivity(), "User List Populated", Toast.LENGTH_SHORT).show();
                Log.d("UserList :" , UserList.toString());
                dataAdapter.notifyDataSetChanged();
            } else if(message.what == 350){
                Toast.makeText(getActivity(), "Cannot Send the Message", Toast.LENGTH_SHORT).show();
            } else if(message.what == 450){
                Toast.makeText(getActivity(), "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                mListener.composeMessage();
            } else if(message.what == 150) {
                Toast.makeText(getActivity(), "Error Populating User List", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
