package com.beans.coder.servicecallsmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.admin.v1beta1.Index;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity implements ResultAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ResultAdapter resultAdapter;
    private List<ServiceCall> serviceCalls;
    private ProgressBar progressBarCircle;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBarCircle = findViewById(R.id.progress_bar_circle);

        serviceCalls = new ArrayList<>();
        resultAdapter = new ResultAdapter(this, serviceCalls);
        resultAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(resultAdapter);

        if (getIntent().hasExtra("Issue") && getIntent().hasExtra("Equipment") &&
                getIntent().hasExtra("ModelNo")) {
            final String selectedIssue = getIntent().getStringExtra("Issue");
            final String selectedEquipment = getIntent().getStringExtra("Equipment");
            final String selectedModelNo = getIntent().getStringExtra("ModelNo");
            db = FirebaseFirestore.getInstance();
            CollectionReference issuesCollectionRef = db.collection("ServiceCalls");
            Query issueQuery = issuesCollectionRef.whereEqualTo("issue", selectedIssue)
                    .whereEqualTo("equipment", selectedEquipment)
                    .whereEqualTo("modelNo", selectedModelNo);
            issueQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    progressBarCircle.setVisibility(View.GONE);
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : list) {

                            ServiceCall call = d.toObject(ServiceCall.class);
                            call.setId(d.getId());
                            serviceCalls.add(call);
                        }
                        resultAdapter.notifyDataSetChanged();
                    }
                }
            });
        }else {
            db = FirebaseFirestore.getInstance();
            db.collection("ServiceCalls").orderBy("callNo").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            progressBarCircle.setVisibility(View.GONE);
                            if (!queryDocumentSnapshots.isEmpty()) {

                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                for (DocumentSnapshot d : list) {

                                    ServiceCall call = d.toObject(ServiceCall.class);
                                    call.setId(d.getId());
                                    serviceCalls.add(call);
                                }
                                resultAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    @Override
    public void onUpdateClick(int position) {
        ServiceCall serviceCall = serviceCalls.get(position);
        Intent intent = new Intent(this,UpdateActivity.class);
        intent.putExtra("ServiceCall", serviceCall);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setTitle("Are you sure to Delete this?");
        builder.setMessage("Deletion is permanent...");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct(position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog ad = builder.create();
        ad.show();
    }

    @Override
    public void onCreatePDFClick(int position) {
        Intent intent = new Intent(this,PDFCreatorActivity.class);
        startActivity(intent);
    }

    private void deleteProduct(int position) {
        ServiceCall serviceCall = serviceCalls.get(position);
        db.collection("ServiceCalls").document(serviceCall.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResultActivity.this, "Product deleted", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(ResultActivity.this, ResultActivity.class));
                        }
                    }
                });
    }
}

