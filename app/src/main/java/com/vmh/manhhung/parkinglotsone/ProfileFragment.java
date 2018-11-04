package com.vmh.manhhung.parkinglotsone;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Button btnUpdate,btnLogout;
    private EditText edtFullName,edtEmail,edtPhone,edtBirthday;
    private CircleImageView imgPhotoManage;
    private static final int PICK_IMAGE=1;

    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        mData= FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        //Anh xa
        btnLogout=(Button)view.findViewById(R.id.btnLogout);
        btnUpdate=(Button)view.findViewById(R.id.btnUpdate);
        edtFullName=(EditText)view.findViewById(R.id.edtFullName);
        edtEmail=(EditText)view.findViewById(R.id.edtEmail);
        edtPhone=(EditText) view.findViewById(R.id.edtPhone);
        edtBirthday=(EditText)view.findViewById(R.id.edtBirtday);
        imgPhotoManage=(CircleImageView)view.findViewById(R.id.imgPhoto);

        LoadData();

        edtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickDates();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpDateData();
            }
        });

        imgPhotoManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(container.getContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void PickDates() {
        final Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                calendar.set(year,month,dayOfMonth);
                Calendar now=Calendar.getInstance();

                if(calendar.getTime().after(now.getTime()))
                {
                    //chon qua thoi gian
                    Log.d("VuHung",""+true);
                    Toast.makeText(getActivity(), "Ngày Sinh không Hợp lệ", Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE)
        {
            imgPhotoManage.setImageURI(data.getData());
        }
    }

    private void UpDateData() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if(user.getProviders().toString().equals("[facebook.com]"))
            {
                String photoUrl;
                StorageReference mountainsRef = storageRef.child("mountains.jpg");
                imgPhotoManage.setDrawingCacheEnabled(true);
                imgPhotoManage.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgPhotoManage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
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
                                    Customer customer1 = new Customer(edtFullName.getText().toString(), edtEmail.getText().toString(), edtPhone.getText().toString(), String.valueOf(uri), edtBirthday.getText().toString());
                                    mData.child("ThongTin").child(user.getUid()).setValue(customer1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext(), "Cập Thật Thành Công", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Cập Thật Thất Bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                Toast.makeText(getContext(), "Sign Up Success", Toast.LENGTH_SHORT).show();
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
                String photoUrl;
                StorageReference mountainsRef = storageRef.child("mountains.jpg");
                imgPhotoManage.setDrawingCacheEnabled(true);
                imgPhotoManage.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgPhotoManage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child("mountains.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                if(user!=null)
                                {
                                    mData.child("ThongTin").child(user.getUid()).child("passWord").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            Customer customer2 = new Customer(edtFullName.getText().toString(), edtEmail.getText().toString(), edtPhone.getText().toString(), String.valueOf(uri), edtBirthday.getText().toString(),dataSnapshot.getValue().toString());
                                            mData.child("ThongTin").child(user.getUid()).setValue(customer2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getContext(), "Cập Thật Thành Công", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Cập Thật Thất Bại", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                Toast.makeText(getContext(), "Sign Up Success", Toast.LENGTH_SHORT).show();
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

    }

    private void LoadData() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        mData.child("ThongTin").child(user.getUid()).child("fullname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtFullName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                edtPhone.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(user.getUid()).child("birthDay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtBirthday.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(user.getUid()).child("uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).into(imgPhotoManage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
