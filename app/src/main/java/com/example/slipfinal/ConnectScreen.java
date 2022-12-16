package com.example.slipfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.*;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ConnectScreen extends AppCompatActivity {

    public static final String Error_Detected = "No NFC Tag Detected";
    public static final String Write_Success = "Text Written Successfully!";
    public static final String Write_Error = "Error during Writing, Try Again!";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writingTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    String userID;
    TextView nfc_contents;
    DatabaseReference database;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String[] Socials;

    String Temp;


    Button ActivateButton;
    Button HomeButton;
    Switch SnapSwitch;
    Switch InstaSwitch;
    Switch FacebookSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_screen);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        database = FirebaseDatabase.getInstance().getReference("UserJava");
        nfc_contents = (TextView) findViewById(R.id.uxConnect_TagContent); // not in use anymore

        ActivateButton =  findViewById(R.id.uxConnect_Activate);
        HomeButton =  findViewById(R.id.uxConnect_HomeButton);
        SnapSwitch =  findViewById(R.id.uxConnect_SnapSwitch);
        InstaSwitch =  findViewById(R.id.uxConnect_InstaSwitch);
        FacebookSwitch =  findViewById(R.id.uxConnect_FacebookSwitch);
        context = this;
        Socials = new String[10];



        ActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(myTag ==null) { // Did not connect to a Tag
                        Toast.makeText(context, Error_Detected, Toast.LENGTH_LONG).show();
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(userID.toString());
                        if (SnapSwitch.isChecked()){sb.append("~Snapchat");}
                        if (InstaSwitch.isChecked()){sb.append("~Instagram");}
                        if (FacebookSwitch.isChecked()){sb.append("~Facebook");}
                        String message = sb.toString(); // creates the final message
                        write(message, myTag);
                        Toast.makeText(context, Write_Success, Toast.LENGTH_LONG ).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG ).show();
                    e.printStackTrace();
                } catch (FormatException e) {
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG ).show();
                    e.printStackTrace();
                }
            }
        });

        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConnectScreen.this,UserHome.class);
                startActivity(intent);
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this, "This device does not support NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        readFromIntent(getIntent());

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE); // please work
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[] { tagDetected };
    }
    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES); // takes the large intent and gets msgs from it
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length]; // new message is the same length
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i]; // converts it to msg
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"



        try {
            // Get the full text of the message - unclean
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            String[] parts = text.split("~"); // message broken up

            String finalText = parts[0]; // user ID at this point

            // make sure the found tag is a real user
            // actual call to populate received info
           database.child(finalText).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DataSnapshot> task) {
                   if(task.isSuccessful()){
                       DataSnapshot userInfo = task.getResult();
                       if(userInfo.exists()){
                           String name = userInfo.child("Username").getValue().toString();

                           Socials[0] = name; // name is 0 index
                           // populate socials from flags
                           for (String flag :parts)
                           {
                               if(flag.equals("Snapchat")){  String snap = userInfo.child("Social Media").child("Snapchat").getValue().toString(); Socials[1]=snap; } // snapchat was sent - always 1
                               if(flag.equals("Instagram")){ String instagram = userInfo.child("Social Media").child("Instagram").getValue().toString();Socials[2]=instagram;} // snapchat was - sent always 2
                               if(flag.equals("Facebook")){ String facebook = userInfo.child("Social Media").child("Facebook").getValue().toString();Socials[3]=facebook;} // snapchat was - sent always 3
                           }
                           Toast.makeText(context, "We Existed", Toast.LENGTH_SHORT).show();
                       }
                       else // no ref in the DB
                       {
                           Toast.makeText(context, "The code seen is not in the DB", Toast.LENGTH_SHORT).show();
                           return;
                       }
                   }
               }
           });
           // creating a contact in our database
           database.child(userID).child("Contact List").child(finalText).child("Contact Name").setValue(Socials[0]);
            for (String flag:parts)
            {
                if(flag.equals("Snapchat")){ database.child(userID).child("Contact List").child(finalText).child("Social Accounts").child("Snapchat").setValue(Socials[1]);  }
                if(flag.equals("Instagram")){ database.child(userID).child("Contact List").child(finalText).child("Social Accounts").child("Instagram").setValue(Socials[2]); }
                if(flag.equals("Facebook")){ database.child(userID).child("Contact List").child(finalText).child("Social Accounts").child("Facebook").setValue(Socials[3]); }
            }

        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        nfc_contents.setText("NFC Content: " + Socials[0]);
    }
    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable Connection
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }
    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }
    //Enable write method
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null);
    }
    //Disable write method
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }


}