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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextCallNo;
    private EditText editTextDate;
    private EditText edittextEquipment;
    private EditText editTextModelNo;
    private EditText editTextIssue;
    private EditText editTextActionTaken;
    private EditText editTextResult;
    private EditText edittextRemarks;

    private Button btnSave;
    private Button search;
    private Button btnImage;
    private Button btnShow;
    private ImageView imageView;
    private Uri imageUri;

    private StorageReference storageReference;
    private StorageTask uploadTask;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCallNo = findViewById(R.id.edittext_call_no);
        editTextDate = findViewById(R.id.edittext_date);
        edittextEquipment = findViewById(R.id.edittext_equipment);
        editTextModelNo = findViewById(R.id.edittext_model_no);
        editTextIssue = findViewById(R.id.edittext_issue);
        editTextActionTaken = findViewById(R.id.edittext_action_taken);
        editTextResult = findViewById(R.id.edittext_result);
        edittextRemarks = findViewById(R.id.edittext_remarks);

        btnSave = findViewById(R.id.button_save);
        search = findViewById(R.id.button_search);
        btnImage = findViewById(R.id.button_image);
        btnShow = findViewById(R.id.button_show);

        imageView = findViewById(R.id.image_view);
        progressBar = findViewById(R.id.progress_bar);

        storageReference= FirebaseStorage.getInstance().getReference("ServiceCalls");
        db = FirebaseFirestore.getInstance();

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(MainActivity.this,"Upload in Progress...",Toast.LENGTH_SHORT).show();
                }else {
                    saveProduct();
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                startActivity(intent);
            }
        });
    }
    public void openFileChooser(){
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
            Picasso.get().load(imageUri).into(imageView);
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private boolean hasValidationErrors(String callNo, String date, String equipment, String modelNo, String issue,
                                        String actionTaken, String result) {
        if (callNo.isEmpty()) {
            editTextCallNo.setError("Call No. required");
            editTextCallNo.requestFocus();
            return true;
        }

        if (date.isEmpty()) {
            editTextDate.setError("Date required");
            editTextDate.requestFocus();
            return true;
        }

        if (equipment.isEmpty()) {
            edittextEquipment.setError("Equipment required");
            edittextEquipment.requestFocus();
            return true;
        }

        if (modelNo.isEmpty()) {
            editTextModelNo.setError("Model No. required");
            editTextModelNo.requestFocus();
            return true;
        }

        if (issue.isEmpty()) {
            editTextIssue.setError("Issue required");
            editTextIssue.requestFocus();
            return true;
        }
        if (actionTaken.isEmpty()) {
            editTextActionTaken.setError("Action Taken required");
            editTextActionTaken.requestFocus();
            return true;
        }
        if (result.isEmpty()) {
            editTextResult.setError("Result required");
            editTextResult.requestFocus();
            return true;
        }
        return false;
    }
    public void  saveProduct(){
        String callNo = editTextCallNo.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String equipment = edittextEquipment.getText().toString().trim();
        String modelNo = editTextModelNo.getText().toString().trim();
        String issue = editTextIssue.getText().toString().trim();
        String actionTaken = editTextActionTaken.getText().toString().trim();
        String result = editTextResult.getText().toString().trim();

        if(hasValidationErrors(callNo,date,equipment,modelNo,issue,actionTaken,result)){
            return;
        }
        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+
                    getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        private static final String TAG ="MainActivity" ;

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            },500);
                            Toast.makeText(MainActivity.this,"Service Call Save Successful",Toast.LENGTH_LONG).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString());

                            String callNo = editTextCallNo.getText().toString().trim();
                            String date = editTextDate.getText().toString().trim();
                            String equipment = edittextEquipment.getText().toString().trim();
                            String modelNo = editTextModelNo.getText().toString().trim();
                            String issue = editTextIssue.getText().toString().trim();
                            String actionTaken = editTextActionTaken.getText().toString().trim();
                            String result = editTextResult.getText().toString().trim();
                            String remarks = edittextRemarks.getText().toString().trim();
                            CollectionReference serviceCalls = db.collection("ServiceCalls");
                            ServiceCall serviceCall = new ServiceCall(
                                    callNo,date,equipment,modelNo,
                                    issue,actionTaken,result,remarks,
                                    downloadUrl.toString());
                            serviceCalls.add(serviceCall);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
}
