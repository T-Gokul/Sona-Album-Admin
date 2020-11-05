package com.gokul.sonaalbumadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddImageActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 1;

    private ActionBar actionBar;
    private EditText EtName;
    private ImageView uploadImage;
    private Button uploadImageBtn;
    private FloatingActionButton addImageBtn;

    ProgressBar progressBar;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask mUploadTask;

    UploadImage upload_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Image");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        EtName = findViewById(R.id.EtName);
        uploadImage = findViewById(R.id.uploadImage);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        addImageBtn = findViewById(R.id.addImageBtn);
        progressBar = findViewById(R.id.progressBar);

        upload_Image = new UploadImage();

        storageReference = FirebaseStorage.getInstance().getReference("Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Images");

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AddImageActivity.this, "Upload in progress", Toast.LENGTH_LONG).show();
                } else {
                    uploadImageFirebase();
                }
            }
        });

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChoose();
            }
        });
    }

    private void showFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(uploadImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImageFirebase() {

        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            mUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String name = EtName.getText().toString().trim();
                                    UploadImage upload_image = new UploadImage(
                                            name,
                                            uri.toString()
                                    );

                                    String uploadID = databaseReference.push().getKey();
                                    databaseReference.child(uploadID).setValue(upload_image);
                                    progressDialog.dismiss();
                                    Toast.makeText(AddImageActivity.this, "Image Uploaded...", Toast.LENGTH_SHORT).show();
                                    uploadImage.setImageResource(R.drawable.ic_upload);
                                    EtName.setText("");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddImageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}