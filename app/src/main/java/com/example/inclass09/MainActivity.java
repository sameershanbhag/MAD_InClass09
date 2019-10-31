/*
 *
 * Name : Sameer Shanbhag
 * Name : Ravina Gaikawad
 * Group1 5
 * MAD InClass 09
 * Topic: Fragments | SharedPreferences | OkHttpClient
 *
 */

package com.example.inclass09;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements DisplayMessage.OnFragmentInteractionListener, MailerFragment.OnFragmentInteractionListener, SignupFragment.OnFragmentInteractionListener, InboxFragment.OnFragmentInteractionListener, ComposeMessage.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mPrefs = getSharedPreferences("USER", MODE_PRIVATE);
        String userToken = mPrefs.getString("userToken", "");

        if(userToken.equals("")){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MailerFragment(), "mailerFragment")
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new InboxFragment(), "InboxFragment")
                    .commit();
        }
    }

    @Override
    public void mailerFragment(String token, String fname, String lname) {
        int mainActivityContainer = findViewById(R.id.container).getId();
        SharedPreferences mPrefs = getSharedPreferences("USER", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("userToken", token);
        prefsEditor.putString("userFname", fname);
        prefsEditor.putString("userLname", lname);
        prefsEditor.apply();
        getSupportFragmentManager().beginTransaction()
                .replace(mainActivityContainer, new InboxFragment(), "inboxFragment")
                .commit();
    }

    @Override
    public void makeSignupFragment() {
        int mainActivityContainer = findViewById(R.id.container).getId();
        getSupportFragmentManager().beginTransaction()
                .replace(mainActivityContainer, new SignupFragment(), "signupFragment")
                .commit();
    }

    @Override
    public void makeSigninFragment() {
        int mainActivityContainer = findViewById(R.id.container).getId();
        getSupportFragmentManager().beginTransaction()
                .replace(mainActivityContainer, new MailerFragment(), "mailerFragment")
                .commit();
    }

    @Override
    public void signupFragment(String token, String fname, String lname) {
        int mainActivityContainer = findViewById(R.id.container).getId();
        SharedPreferences mPrefs = getSharedPreferences("USER", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("userToken", token);
        prefsEditor.putString("userFname", fname);
        prefsEditor.putString("userLname", lname);
        prefsEditor.apply();
        getSupportFragmentManager().beginTransaction()
                .replace(mainActivityContainer, new InboxFragment(), "inboxFragment")
                .commit();
    }

    @Override
    public void composeMailFragment() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new ComposeMessage(), "composeFragment")
            .commit();
    }

    @Override
    public void composeMessage() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new InboxFragment(), "InboxFragment")
            .commit();
    }

    @Override
    public void displayMessageDone() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new InboxFragment(), "InboxFragment")
            .commit();
    }

    @Override
    public void viewMail(Messages currentMessage){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new DisplayMessage(currentMessage), "displayFragment")
                .commit();
    }
}
