package com.example.finalmessageapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.finalmessageapp.Adapter.MessageAdapter;
import com.example.finalmessageapp.Models.MessageData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MesajOlustur extends Fragment {


    Button CreateMessage;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextInputEditText MessageName,MessageDesc;
    FirebaseFirestore firebaseFirestore;

    RecyclerView recyclerView;
    ArrayList<MessageData> messageDataArrayList;
    MessageAdapter messageAdapter;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mesaj_olustur, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        CreateMessage = rootView.findViewById(R.id.mesajolustur);
        MessageName = rootView.findViewById(R.id.mesajadi);
        MessageDesc = rootView.findViewById(R.id.mesaj);

        recyclerView = rootView.findViewById(R.id.mesajlar);
        LinearLayoutManager lls = new LinearLayoutManager(getContext());
        lls.setOrientation(LinearLayoutManager.VERTICAL);
        lls.setReverseLayout(false);
        recyclerView.setLayoutManager(lls);

        messageDataArrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageDataArrayList,getContext());
        recyclerView.setAdapter(messageAdapter);

        LoadData();


        CreateMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(MessageName.getText().toString()) || TextUtils.isEmpty(MessageDesc.getText().toString())){
                    Toast.makeText(getActivity(), "Lütfen Mesaj Adı ve Mesajı Giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userid = firebaseUser.getUid();

                MessageData messageData = new MessageData(userid,MessageName.getText().toString(),MessageDesc.getText().toString());

                firebaseFirestore.collection("message")
                        .add(messageData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getActivity(), "Mesaj Oluşturuldu", Toast.LENGTH_SHORT).show();
                            messageDataArrayList.clear();
                            LoadData();
                        });
            }
        });


        return rootView;
    }

    private void LoadData(){

        firebaseFirestore.collection("message")
                .whereEqualTo("userid",String.valueOf(firebaseUser.getUid()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            messageDataArrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MessageData messageData = document.toObject(MessageData.class);
                                messageDataArrayList.add(messageData);
                            }
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }
}