package com.example.finalmessageapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalmessageapp.Adapter.ContactsAdapter;
import com.example.finalmessageapp.Adapter.GroupAddMember;
import com.example.finalmessageapp.Interface.ContactsListener;
import com.example.finalmessageapp.Interface.GroupAddMemberListener;
import com.example.finalmessageapp.Models.Contacts;
import com.example.finalmessageapp.Models.ContactsToGroup;
import com.example.finalmessageapp.Models.GroupModels;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;


public class GrubaUyeEkle extends Fragment implements GroupAddMemberListener, ContactsListener {

    RecyclerView recyclerView,recyclerViewTwo;
    ArrayList<GroupModels> groupModelsArrayList;
    GroupAddMember groupAddMemberAdapter;
    TextView SelectedGroup;

    List<Contacts> contactsList;
    ContactsAdapter contactsAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    String SeciliGurupid=null;
    String SeciliGurupAdi=null;
    String SecilenKisiNumarasi=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        contactsList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(getContext(), contactsList, this::OnItemClicked);

        LoadData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gruba_uye_ekle, container, false);

        SelectedGroup = rootView.findViewById(R.id.selectedgroupname);

        recyclerView = rootView.findViewById(R.id.guruplistesi);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(recyclerView.HORIZONTAL);
        llm.setReverseLayout(false);
        recyclerView.setLayoutManager(llm);

        recyclerViewTwo = rootView.findViewById(R.id.rehberdekiler);
        LinearLayoutManager llms = new LinearLayoutManager(getActivity());
        llms.setOrientation(recyclerViewTwo.VERTICAL);
        llms.setReverseLayout(false);
        recyclerViewTwo.setLayoutManager(llms);

        groupModelsArrayList = new ArrayList<>();
        groupAddMemberAdapter = new GroupAddMember(groupModelsArrayList,getContext(),this::onItemClicked);
        recyclerView.setAdapter(groupAddMemberAdapter);
        recyclerViewTwo.setAdapter(contactsAdapter);

        Dexter.withActivity(getActivity())
                .withPermission(android.Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(response.getPermissionName().equals(Manifest.permission.READ_CONTACTS)){
                            getContacts();
                        }
                    }

                    private void getContacts() {

                        Cursor phones = getActivity().getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                        while(phones.moveToNext()){
                            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String photo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                            Contacts contacts = new Contacts(name,number,photo);
                            contactsList.add(contacts);
                            contactsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getContext(), "İzin Verilmedi", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.cancelPermissionRequest();
                    }
                }).check();

        return rootView;
    }

    private void LoadData(){
        firebaseFirestore.collection("group")
                .whereEqualTo("userid",String.valueOf(firebaseUser.getUid()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            groupModelsArrayList.clear();
                            for(QueryDocumentSnapshot doc:task.getResult()){
                                GroupModels models = doc.toObject(GroupModels.class);
                                groupModelsArrayList.add(models);
                                groupAddMemberAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    @Override
    public void onItemClicked(GroupModels groupModels) {
        SeciliGurupid = groupModels.getGroupid();
        SeciliGurupAdi = groupModels.getGroupname();
        SelectedGroup.setText(String.valueOf("Seçilen Grup : "+SeciliGurupAdi));
    }

    @Override
    public void OnItemClicked(Contacts contacts) {

        SecilenKisiNumarasi = contacts.getPhone();

        if(SecilenKisiNumarasi == null){
            Toast.makeText(getContext(), "Lütfen bir kişi seçiniz", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Gruba Üye Ekle");
        builder.setMessage(SeciliGurupAdi + " grubuna " + SecilenKisiNumarasi + " numaralı kişiyi eklemek istediğinize emin misiniz?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String userid = firebaseUser.getUid();

                ContactsToGroup contactsToGroup = new ContactsToGroup(SeciliGurupid,userid,SecilenKisiNumarasi);

                firebaseFirestore.collection("contactsToGroup")
                        .add(contactsToGroup)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Kişi Gruba Eklendi", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();


    }
}