package com.beans.coder.servicecallsmanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<String> equipmentList;
    private ArrayAdapter<String> equipmentAdapter;
    private ListView equipmentListView;

    private ArrayList<String> modelNoList;
    private ArrayAdapter<String> modelNoAdapter;
    private ListView modelNoListView;

    private ArrayList<String> issueList;
    private ArrayAdapter<String> issueAdapter;
    private ListView issueListView;

    private String selectedEquipment;
    private String selectedModelNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        equipmentListView = findViewById(R.id.list_view_equipment);
        equipmentList = new ArrayList<>();

        modelNoListView = findViewById(R.id.list_view_model_no);
        modelNoList = new ArrayList<>();

        issueListView = findViewById(R.id.list_view_issue);
        issueList = new ArrayList<>();

        equipmentAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,equipmentList);
        equipmentListView.setAdapter(equipmentAdapter);
        showEquipments();
        equipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedEquipment = equipmentList.get(position);
                showModelNos(selectedEquipment);
            }
        });

        modelNoAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,modelNoList);
        modelNoListView.setAdapter(modelNoAdapter);
        modelNoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedModelNo = modelNoList.get(position);
                showIssues(selectedModelNo);
            }
        });

        issueAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,issueList);
        issueListView.setAdapter(issueAdapter);
        issueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedIssue = issueList.get(position);
                Intent intent = new Intent(SearchActivity.this,ResultActivity.class);
                intent.putExtra("Issue",selectedIssue);
                intent.putExtra("Equipment",selectedEquipment);
                intent.putExtra("ModelNo", selectedModelNo);
                startActivity(intent);
            }
        });
    }

    private void showEquipments() {
        db = FirebaseFirestore.getInstance();
        db.collection("ServiceCalls").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                ServiceCall call = d.toObject(ServiceCall.class);
                                call.setId(d.getId());
                                ServiceCall serviceCall = new ServiceCall();
                                serviceCall.setEquipment(call.getEquipment());
                                String equipment = serviceCall.getEquipment();
                                // To eliminate duplicates
                                if(!equipmentList.contains(equipment))
                                    equipmentList.add(equipment);
                            }
                            equipmentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    public void showModelNos(String item){
        modelNoList.clear();
        db = FirebaseFirestore.getInstance();
        CollectionReference equipmentsCollectionRef = db.collection("ServiceCalls");
        Query equipmentQuery = equipmentsCollectionRef.whereEqualTo("equipment",item);
                equipmentQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                ServiceCall call = d.toObject(ServiceCall.class);
                                call.setId(d.getId());
                                ServiceCall serviceCall = new ServiceCall();
                                serviceCall.setModelNo(call.getModelNo());
                                String modelNo = serviceCall.getModelNo();
                                // To eliminate duplicates
                                if(!modelNoList.contains(modelNo))
                                    modelNoList.add(modelNo);
                            }
                            modelNoAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    public void showIssues(String item){
        issueList.clear();
        db = FirebaseFirestore.getInstance();
        CollectionReference issuesCollectionRef = db.collection("ServiceCalls");
        Query issueQuery = issuesCollectionRef.whereEqualTo("modelNo",item);
        issueQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot d : list) {

                        ServiceCall call = d.toObject(ServiceCall.class);
                        call.setId(d.getId());
                        ServiceCall serviceCall = new ServiceCall();
                        serviceCall.setIssue(call.getIssue());
                        String issue = serviceCall.getIssue();
                        // To eliminate duplicates
                        if(!issueList.contains(issue))
                            issueList.add(issue);
                    }
                    issueAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
