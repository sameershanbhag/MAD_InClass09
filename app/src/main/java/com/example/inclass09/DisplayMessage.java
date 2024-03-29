package com.example.inclass09;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;


public class DisplayMessage extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Messages displayMessage;

    private TextView tv_subject;
    private TextView tv_from;
    private TextView tv_to;
    private TextView tv_message;
    private Button bt_back;

    public DisplayMessage(Messages message) {
        // Required empty public constructor
        this.displayMessage = message;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Display Message");
        View view = inflater.inflate(R.layout.fragment_display_message, container, false);

        tv_subject = view.findViewById(R.id.tv_subject);
        tv_from = view.findViewById(R.id.tv_from);
        tv_to = view.findViewById(R.id.tv_to);
        tv_message = view.findViewById(R.id.tv_message);
        bt_back = view.findViewById(R.id.bt_back);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(displayMessage != null){
            if(displayMessage.getSubject() != null){
                tv_subject.setText(displayMessage.getSubject());
            }

            if(displayMessage.getSenderFname() != null &&  displayMessage.getSenderLname() != null){
                tv_from.setText(String.format("%s %s", displayMessage.getSenderFname(), displayMessage.getSenderLname()));
            }

            if(displayMessage.getCreatedAt() != null){
                tv_to.setText(displayMessage.getCreatedAt());
            }

            if(displayMessage.getMessage() != null){
                tv_message.setText(displayMessage.getMessage());
            }

        }

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.displayMessageDone();
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

    public interface OnFragmentInteractionListener {
        void displayMessageDone();
    }
}
