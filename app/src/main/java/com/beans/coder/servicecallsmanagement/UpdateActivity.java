package com.beans.coder.servicecallsmanagement;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextCallNo;
    private EditText editTextDate;
    private EditText edittextEquipment;
    private EditText editTextModelNo;
    private EditText editTextIssue;
    private EditText editTextActionTaken;
    private EditText editTextResult;
    private EditText edittextRemarks;

    private Button btnUpdate;
    private Button btnNext;
    private Button btnPrev;
    private Button btnNextTen;
    private ImageView uImageView;
    private String uImageUri;
    private Uri imageUri;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private StorageTask uploadTask;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private ServiceCall call;
    private List<ServiceCall> serviceCalls;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        editTextCallNo = findViewById(R.id.update_edittext_call_no);
        editTextDate = findViewById(R.id.update_edittext_date);
        edittextEquipment = findViewById(R.id.update_edittext_equipment);
        editTextModelNo = findViewById(R.id.update_edittext_model_no);
        editTextIssue = findViewById(R.id.update_edittext_issue);
        editTextActionTaken = findViewById(R.id.update_edittext_action_taken);
        editTextResult = findViewById(R.id.update_edittext_result);
        edittextRemarks = findViewById(R.id.update_edittext_remarks);

        btnUpdate = findViewById(R.id.button_update);
        btnNext = findViewById(R.id.button_next);
        btnPrev = findViewById(R.id.button_prev);
        btnNextTen = findViewById(R.id.button_next_ten);

        uImageView = findViewById(R.id.update_image_view);
        progressBar = findViewById(R.id.update_progress_bar);
        serviceCalls = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference("ServiceCalls");

        db = FirebaseFirestore.getInstance();
        db.collection("ServiceCalls").orderBy("callNo").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                ServiceCall call = d.toObject(ServiceCall.class);
                                call.setId(d.getId());
                                serviceCalls.add(call);
                            }
                        }
                    }
                });
        if(getIntent().hasExtra("ServiceCall")) {
            call = (ServiceCall) getIntent().getSerializableExtra("ServiceCall");
            showServiceCall(call);
        }
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCall();
            }
        });
        uImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev();
            }
        });
        btnNextTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTen();
            }
        });
    }

    private void nextTen() {
        i = i + 9;
        if(i>serviceCalls.size()) {
            i = serviceCalls.size() - 1;
            System.out.println("i = "+i);
            Toast toast = Toast.makeText(this,"Last Photo of List",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        ServiceCall serviceCall = serviceCalls.get(i);
        showServiceCall(serviceCall);
    }

    private void prev() {
        i = i - 1;
        if(i<0) {
            i = 0;
            Toast toast = Toast.makeText(this,"First Photo of List",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        ServiceCall serviceCall = serviceCalls.get(i);
        showServiceCall(serviceCall);
    }

    private void next() {
        i = i + 1;
        if(i>serviceCalls.size()-1) {
            i = serviceCalls.size() - 1;
            Toast toast = Toast.makeText(this,"Last Service Call of List",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        ServiceCall serviceCall = serviceCalls.get(i);
        showServiceCall(serviceCall);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(uImageView);
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void updateCall() {
        String callNo = editTextCallNo.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String equipment = edittextEquipment.getText().toString().trim();
        String modelNo = editTextModelNo.getText().toString().trim();
        String issue = editTextIssue.getText().toString().trim();
        String actionTaken = editTextActionTaken.getText().toString().trim();
        String result = editTextResult.getText().toString().trim();
        String remarks = edittextRemarks.getText().toString().trim();

        final ServiceCall serviceCall = new ServiceCall();
         serviceCall.setCallNo(callNo);
         serviceCall.setDate(date);
         serviceCall.setEquipment(equipment);
         serviceCall.setModelNo(modelNo);
         serviceCall.setIssue(issue);
         serviceCall.setActionTaken(actionTaken);
         serviceCall.setResult(result);
         serviceCall.setRemarks(remarks);

        if(uImageUri != null){
            StorageReference imageRef = storage.getReferenceFromUrl(call.getImageUri());
            imageRef.delete();
        }
        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+
                    getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        private static final String TAG ="UpdateActivity" ;

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            },500);

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString());
                            db.collection("ServiceCalls").document(call.getId())
                                    .update(
                                            "callNo", serviceCall.getCallNo(),
                                            "date", serviceCall.getDate(),
                                            "equipment", serviceCall.getEquipment(),
                                            "modelNo", serviceCall.getModelNo(),
                                            "issue", serviceCall.getIssue(),
                                            "actionTaken", serviceCall.getActionTaken(),
                                            "result", serviceCall.getResult(),
                                            "remarks", serviceCall.getRemarks(),
                                            "imageUri",downloadUrl.toString()
                                    )
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UpdateActivity.this, "Service Call Updated", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = 100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int)progress);
                        }
                    });
        }else {
            Toast.makeText(this,"NO File Selected",Toast.LENGTH_SHORT).show();
        }
    }
    public void showServiceCall(ServiceCall call){
        editTextCallNo.setText(call.getCallNo());
        editTextDate.setText(call.getDate());
        edittextEquipment.setText(call.getEquipment());
        editTextModelNo.setText(call.getModelNo());
        editTextIssue.setText(call.getIssue());
        editTextActionTaken.setText(call.getActionTaken());
        editTextResult.setText(call.getResult());
        edittextRemarks.setText(call.getRemarks());
        uImageUri = call.getImageUri();
        Picasso.get().load(uImageUri).into(uImageView);
    }
}
