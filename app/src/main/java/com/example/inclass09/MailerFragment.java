package com.example.inclass09;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class MailerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private final String LOGIN_URL = "http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login";

    private EditText et_email;
    private EditText et_password;
    private Button bt_login;
    private Button bt_signup;
    private final OkHttpClient client = new OkHttpClient();

    public MailerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Mailer");
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPost(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Yo", "I Am here");
                        Message message = mHandler.obtainMessage(400, "Fail");
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("Login Response", response.body().toString());
                        if (response.isSuccessful()) {
                            try {
                                JSONObject root = new JSONObject(response.body().string());
                                String token = root.getString("token");
                                String fname = root.getString("user_fname");
                                String lname = root.getString("user_lname");
                                mListener.mailerFragment(token, fname, lname);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            JSONObject root;
                            String errorMessage = "Something went Wrong";
                            try {
                                root = new JSONObject(response.body().string());
                                errorMessage = root.getString("message");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Message message = mHandler.obtainMessage(400, errorMessage);
                            message.sendToTarget();
                        }
                    }
                });
            }
        });

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.makeSignupFragment();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Mailer");
        View view = inflater.inflate(R.layout.fragment_mailer, container, false);

        et_email = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_password);
        bt_login = view.findViewById(R.id.bt_login);
        bt_signup = view.findViewById(R.id.bt_signup);

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

    Call loginPost(Callback callback) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", et_email.getText().toString())
                .add("password", et_password.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public interface OnFragmentInteractionListener {
        void mailerFragment(String token, String fname, String lname);
        void makeSignupFragment();
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if(message.what == 400){
                Toast.makeText(getActivity(), message.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
