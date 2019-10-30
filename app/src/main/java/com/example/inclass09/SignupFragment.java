package com.example.inclass09;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

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

public class SignupFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private final String SIGNUP_URL = "http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup";
    private final OkHttpClient client = new OkHttpClient();

    private EditText et_fname;
    private EditText et_lname;
    private EditText et_pass;
    private EditText et_cpass;
    private EditText et_email;
    private Button bt_cancle;
    private Button bt_signup;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Sign Up");

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        et_fname = view.findViewById(R.id.et_fname);
        et_lname = view.findViewById(R.id.et_lname);
        et_pass = view.findViewById(R.id.et_pass);
        et_cpass = view.findViewById(R.id.et_cpass);
        et_email = view.findViewById(R.id.et_email);
        bt_cancle = view.findViewById(R.id.bt_cancle);
        bt_signup = view.findViewById(R.id.bt_signup);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_pass.getText().toString().equals(et_cpass.getText().toString()) && et_pass.getText().toString().length() < 6) {
                    et_pass.setError("Enter a Valid Password");
                    et_cpass.setError("Enter a Valid Password");
                } else if(et_fname.getText().toString().equals("") || et_fname.getText().toString().trim().length() == 0){
                    et_fname.setError("Enter a Valid Name");
                } else if(et_lname.getText().toString().equals("") || et_lname.getText().toString().trim().length() == 0){
                    et_lname.setError("Enter a Valid Name");
                } else if(et_email.getText().toString().equals("") || et_email.getText().toString().trim().length() == 0){
                    et_email.setError("Enter a Valid Email");
                } else {
                    signupPost(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message message = mHandler.obtainMessage(400, "Fail");
                            message.sendToTarget();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject root = new JSONObject(response.body().string());
                                    String token = root.getString("token");
                                    String fname = root.getString("user_fname");
                                    String lname = root.getString("user_lname");
                                    mListener.signupFragment(token, fname, lname);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } else {
                                Message message = mHandler.obtainMessage(400, "Fail");
                                message.sendToTarget();
                                Log.d("Error", response.body().string());
                            }
                        }
                    });
                }
            }
        });

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.makeSigninFragment();
            }
        });

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

    Call signupPost(Callback callback) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", et_email.getText().toString())
                .add("password", et_pass.getText().toString())
                .add("fname", et_fname.getText().toString())
                .add("lname", et_lname.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url(SIGNUP_URL)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public interface OnFragmentInteractionListener {
        void signupFragment(String token, String fname, String lname);
        void makeSigninFragment();
    }


    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if(message.what == 400){
                Toast.makeText(getActivity(), "Please Check your Credentials", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
