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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageUsersActivity extends AppCompatActivity {

    private CircleImageView imgAnh;
    private EditText edtFullName,edtEmail,edtPhone,edtBirthday;
    private Button btnUpdate,btnDelete,btnBack;
    private String mUserId;
    private DatabaseReference mData;
    private static final int PICK_IMAGE=1;
    FirebaseStorage storage;
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        AnhXa();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mData= FirebaseDatabase.getInstance().getReference();
        mUserId=getIntent().getStringExtra("user_id");
        Log.d("VuHung",""+mUserId);

        mData.child("ThongTin").child(mUserId).child("fullname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtFullName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(mUserId).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtEmail.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(mUserId).child("phoneNumber").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtPhone.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(mUserId).child("birthDay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtBirthday.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(mUserId).child("uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).into(imgAnh);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickDates();
            }
        });

        imgAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManageUsersActivity.this,ManageAccountActivity.class);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.child("ThongTin").child(mUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ManageUsersActivity.this, "Xóa Thành công", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(ManageUsersActivity.this,ManageAccountActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(ManageUsersActivity.this, "Xóa Thất Bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String photoUrl;
                StorageReference mountainsRef = storageRef.child("mountains.jpg");
                imgAnh.setDrawingCacheEnabled(true);
                imgAnh.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgAnh.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ManageUsersActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child("mountains.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if(mUserId!=null)
                                {
                                    Customer customer1 = new Customer(edtFullName.getText().toString(), edtEmail.getText().toString(), edtPhone.getText().toString(), String.valueOf(uri), edtBirthday.getText().toString());
                                    mData.child("ThongTin").child(mUserId).child("fullname").setValue(edtFullName.getText().toString());
                                    mData.child("ThongTin").child(mUserId).child("email").setValue(edtEmail.getText().toString());
                                    mData.child("ThongTin").child(mUserId).child("phoneNumber").setValue(edtPhone.getText().toString());
                                    mData.child("ThongTin").child(mUserId).child("birthDay").setValue(edtBirthday.getText().toString());
                                    mData.child("ThongTin").child(mUserId).child("uri").setValue(String.valueOf(uri));
                                }
                                Toast.makeText(ManageUsersActivity.this, "Cập Nhật Thành Công", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ManageUsersActivity.this,ManageAccountActivity.class);
                                startActivity(intent);
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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE)
        {
            imgAnh.setImageURI(data.getData());
        }
    }

    private void PickDates() {
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
                    Toast.makeText(ManageUsersActivity.this, "Ngày Sinh không Hợp lệ", Toast.LENGTH_SHORT).show();
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

    private void AnhXa() {
        imgAnh=(CircleImageView)findViewById(R.id.imgAnh);
        edtFullName=(EditText)findViewById(R.id.edtFullName);
        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtPhone=(EditText)findViewById(R.id.edtPhone);
        edtBirthday=(EditText)findViewById(R.id.edtBirthday);
        btnUpdate=(Button)findViewById(R.id.btnUpdate);
        btnDelete=(Button)findViewById(R.id.btnDelete);
        btnBack=(Button)findViewById(R.id.btnback);
    }
}
