package com.vmh.manhhung.parkinglotsone;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {
    EditText edtFullName,edtEmail,edtPassword,edtBirthday,edtphone;
    Button btnRegistration,btnBacktoLogin;
    ImageView imgPhoto;
    DatabaseReference mData;
    Customer customer;
    FirebaseStorage storage;
    static final int PICK_IMAGE=1;
    FirebaseAuth mAuthencation;
    StorageReference storageRef;
    Uri imageUri;

    int ketqua;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuthencation=FirebaseAuth.getInstance();
        mData= FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        AnhXa();
        imageUri=null;
        storageRef = storage.getReference();

        btnBacktoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registration();
            }
        });
        edtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickDates();
            }
        });
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
            }
        });
    }
    private void PickDates()
    {
        final Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                calendar.set(year,month,dayOfMonth);

                Calendar now=Calendar.getInstance();

                if(calendar.getTime().after(now.getTime()))
                {
                    //chon qua thoi gian
                    Log.d("VuHung",""+true);
                    Toast.makeText(RegistrationActivity.this, "Ngày Sinh không Hợp lệ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //chon dung thoi gian
                    Log.d("VuHung",""+false);
                    edtBirthday.setText(simpleDateFormat.format(calendar.getTime()));
                }
            }
        },year,month,day);
        datePickerDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE)
        {

            imageUri=data.getData();
            imgPhoto.setImageURI(imageUri);
        }
    }

    private void AnhXa() {
        btnBacktoLogin=(Button)findViewById(R.id.btnBacktoLogin);
        imgPhoto=(ImageView)findViewById(R.id.imgPhotoRisg);
        edtFullName=(EditText)findViewById(R.id.edtFullName);
        edtphone=(EditText)findViewById(R.id.edtPhoneNumber);
        edtBirthday=(EditText)findViewById(R.id.edtBirthDay);
        edtEmail        =   (EditText)findViewById(R.id.edtEmail);
        edtPassword     =   (EditText)findViewById(R.id.edtPassword);
        btnRegistration =   (Button)findViewById(R.id.btnRegistration);
    }
    private  void Registration()
    {

        final String fullname=edtFullName.getText().toString();
        final String phone=edtphone.getText().toString();
        final String birthday=edtBirthday.getText().toString();
        final String email=edtEmail.getText().toString();
        final String password=edtPassword.getText().toString();

        if(fullname.equals("") || phone.equals("") || birthday.equals("") || email.equals("") || password.equals(""))
        {
            Toast.makeText(this, "Không Được Để Trống Thông Tin", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(0!=0)
            {
                Toast.makeText(this, "Xin Cập Nhật Ảnh Đại Diện", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mAuthencation.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String photoUrl;
                                    StorageReference mountainsRef = storageRef.child("mountains.jpg");
                                    imgPhoto.setDrawingCacheEnabled(true);
                                    imgPhoto.buildDrawingCache();
                                    Bitmap bitmap = ((BitmapDrawable) imgPhoto.getDrawable()).getBitmap();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    UploadTask uploadTask = mountainsRef.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(RegistrationActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            storageRef.child("mountains.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                                    if(user!=null)
                                                    {
                                                        Customer customer=new Customer(fullname,email,phone,String.valueOf(uri),birthday,password);
                                                        Intent intent=new Intent(RegistrationActivity.this,LoginResActivity.class);
                                                        mData.child("ThongTin").child(user.getUid()).setValue(customer);
                                                        startActivity(intent);
                                                    }
                                                    Toast.makeText(RegistrationActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Log.d("Error",""+exception);
                                                }
                                            });
                                        }
                                    });

                                }
                                else
                                {
                                    Toast.makeText(RegistrationActivity.this, "Registration Failed\n Email và Password không hợp lệ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }

    }
}

