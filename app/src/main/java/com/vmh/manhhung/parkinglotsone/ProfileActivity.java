package com.vmh.manhhung.parkinglotsone;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class ProfileActivity extends AppCompatActivity {
    EditText edtFullName,edtEmail,edtPhone,edtBirthDay;
    CircleImageView imgPhoto;
    Uri imageUri;
    Button btnUpdate,btnLogout,btnChangePassword;
    private DatabaseReference mData;
    FirebaseAuth mAuth;
    static final int PICK_IMAGE=1;
    int REQUEST_CODE_IMAGE=1;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AnhXa();
        imageUri=null;
        final Intent intent = getIntent();
        final Customer customer = (Customer) intent.getSerializableExtra("ThongTin");
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Provider",user.getProviders().toString());
        if(user.getProviders().toString().equals("[facebook.com]"))
        {
            btnChangePassword.setVisibility(View.INVISIBLE);
        }

        mData.child("ThongTin").child(user.getUid()).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtEmail.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mData.child("ThongTin").child(user.getUid()).child("phoneNumber").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString()!="")
                {
                    edtPhone.setText(dataSnapshot.getValue().toString());
                }
                else
                {
                    edtPhone.setHint("Phone Number");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(user.getUid()).child("birthDay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString()!="" )
                {
                    edtBirthDay.setText(dataSnapshot.getValue().toString());
                }
                else
                {
                    edtBirthDay.setHint("Birthday");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mData.child("ThongTin").child(user.getUid()).child("fullname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtFullName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mData.child("ThongTin").child(user.getUid()).child("uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).into(imgPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    String photoUrl;
                    StorageReference mountainsRef = storageRef.child("mountains.jpg");
                    imgPhoto.setDrawingCacheEnabled(true);
                    imgPhoto.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgPhoto.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ProfileActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.child("mountains.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if(user!=null)
                                    {
                                        Log.d("vuhung",""+edtPhone.getText().toString());
                                        Customer customer1 = new Customer(edtFullName.getText().toString(), edtEmail.getText().toString(), edtPhone.getText().toString(), String.valueOf(uri), edtBirthDay.getText().toString());
                                        mData.child("ThongTin").child(user.getUid()).child("fullname").setValue(edtFullName.getText().toString());
                                        mData.child("ThongTin").child(user.getUid()).child("email").setValue(edtEmail.getText().toString());
                                        mData.child("ThongTin").child(user.getUid()).child("phoneNumber").setValue(edtPhone.getText().toString());
                                        mData.child("ThongTin").child(user.getUid()).child("birthDay").setValue(edtBirthDay.getText().toString());
                                        mData.child("ThongTin").child(user.getUid()).child("uri").setValue(String.valueOf(uri));
                                    }
                                    Toast.makeText(ProfileActivity.this, "Cập Nhật Thành Công", Toast.LENGTH_SHORT).show();
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
                }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogChangePW();
            }
        });
        Button btnLogout = (Button)findViewById(R.id.btnLogOut);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                sendToStart();
            }
        });
        edtBirthDay.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PICK_IMAGE)
        {
            imageUri=data.getData();
            imgPhoto.setImageURI(imageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void sendToStart()
    {
        Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
        {
            sendToStart();
        }
    }

    private void AnhXa() {
        edtBirthDay     =(EditText)findViewById(R.id.edtBirthDay);
        edtEmail        =(EditText)findViewById(R.id.edtEmail);
        edtFullName     =(EditText)findViewById(R.id.edtFullName);
        edtPhone        =(EditText)findViewById(R.id.edtPhone);
        imgPhoto        =(CircleImageView)findViewById(R.id.imageView);
        btnUpdate       =(Button)findViewById(R.id.btnUpdate);
        btnChangePassword=(Button)findViewById(R.id.btnChangesPassword);

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
                    Toast.makeText(ProfileActivity.this, "Ngày Sinh không Hợp lệ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //chon dung thoi gian
                    Log.d("VuHung",""+false);
                    edtBirthDay.setText(simpleDateFormat.format(calendar.getTime()));
                }
            }
        },year,month,day);
        datePickerDialog.show();
    }
    private void DialogChangePW()
    {
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_changepw_custom);
        final EditText edtCurrentPW=(EditText)dialog.findViewById(R.id.edtPWCurrent);
        final EditText edtNewPW=(EditText)dialog.findViewById(R.id.edtNewPW);
        final EditText edtVerifyPW=(EditText)dialog.findViewById(R.id.edtVerifyPW);
        Button btnSave=(Button)dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                mData.child("ThongTin").child(user.getUid()).child("passWord").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("Password",dataSnapshot.getValue().toString());
                        if(dataSnapshot.getValue().toString().equals(edtCurrentPW.getText().toString())&& edtNewPW.getText().toString().equals(edtVerifyPW.getText().toString()))
                        {
                            user.updatePassword(edtNewPW.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                mData.child("ThongTin").child(user.getUid()).child("passWord").setValue(edtNewPW.getText().toString());
                                                Toast.makeText(ProfileActivity.this, "Update Successful\n", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(ProfileActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(ProfileActivity.this, "The current password is wrong or New passwords do not match or New passwords are not formatted correctly", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        dialog.show();
    }

}
