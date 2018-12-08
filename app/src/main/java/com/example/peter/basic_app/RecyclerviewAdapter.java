package com.example.peter.basic_app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.peter.basic_app.AddActivity.CAMERA_REQUEST_CODE;
import static com.example.peter.basic_app.HomeActivity.check;
import static com.example.peter.basic_app.HomeActivity.dateFormatter;
import static com.example.peter.basic_app.HomeActivity.getDateDiff;
import static com.example.peter.basic_app.HomeActivity.getMembershipFromFirebaseDatabase;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

    List<Users> listdata;
    DatabaseReference mRef;
    Object checkedItem;
    String theFinalDate;
    DatePickerDialog.OnDateSetListener chooseDate;
    byte[] mData;
    UploadTask uploadTask;
    ProgressDialog mProgressDialog;
    StorageReference mStorageRef;
    Context context;
    ImageView imageIbUpdateDialog;




    public RecyclerviewAdapter(List<Users> listdata, Context context) {

        this.listdata = listdata;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerviewAdapter.MyViewHolder myViewHolder, int i) {
        final Users data = listdata.get(i);
        myViewHolder.user_name.setText(data.getName());
        myViewHolder.user_membership.setText(" نظام الأشتراك : " + getMembershipFromFirebaseDatabase(data.getMembership()));
        if (data.getImage() == null){
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/test-project-798ce.appspot.com/o/Unknown_avatar.png?alt=media&token=263bc75c-cffe-4a48-8339-b4dcf6250054").fit().transform(new CircleTransform()).centerCrop().into(myViewHolder.userImage);
        }else{
            Picasso.get().load(data.getImage()).fit().transform(new CircleTransform()).centerCrop().into(myViewHolder.userImage);
        }
       /*
       *  if (data.getNotes().matches("0")){
            myViewHolder.notesSection.setText("لا يوجد ملاحظات");
        }else {
            myViewHolder.notesSection.setText(" ملاحظات : " + String.valueOf(data.getNotes()));
        }
        */

        //Font
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Questv1-Bold.otf");
        myViewHolder.user_name.setTypeface(typeface);
        myViewHolder.user_membership.setTypeface(typeface);
        myViewHolder.notesSection.setTypeface(typeface);

        String startDate = data.getStartdate();
        String todayDate = dateFormatter(System.currentTimeMillis(), "MM/dd/yyyy");

        final Date date1 = new Date(startDate); //  Month/Day/Year
        Date date2 = new Date(todayDate);

        final long diff = getDateDiff(date1, date2);
        myViewHolder.user_status.setImageResource(check(diff, data.getMembership()));

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diff <= 0){
                    Toast.makeText(v.getContext(),  " لم يبدأ أشتراك " + "'"+data.getName()+"'" + " بعد ", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(v.getContext(), data.getName() +" مشترك منذ "+ String.valueOf(diff) + " يوم علي نظام " + getMembershipFromFirebaseDatabase(data.getMembership()), Toast.LENGTH_LONG).show();
                }

            }
        });


        myViewHolder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();

                Intent intent = new Intent(context, RenewMembershipActivity.class);
                intent.putExtra("childKey", data.getKey());
                intent.putExtra("userName", data.getName());
                context.startActivity(intent);
            }
        });


        //update user information
        myViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UpdateUserInfoActivity.class);
                intent.putExtra("childKey", data.getKey());
                intent.putExtra("userName", data.getName());
                intent.putExtra("userAvatar", data.getImage());
                v.getContext().startActivity(intent);
            }
        });



        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final Context context = v.getContext();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                //LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                //builder.setView(inflater.inflate(R.layout.update_dialog, null));

                builder.setTitle(" حذف العضو " + data.getName() +" ؟ ");

                builder.setPositiveButton("حذف ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
                        mRef.child(data.getKey()).removeValue();
                        Toast.makeText(context,  "تم حذف "+data.getName(), Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView user_name, user_membership;
        ImageView user_status;
        Button updateBtn;
        Button editNotes;
        TextView notesSection;
        ImageView userImage;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.user_name_edit_text);
            user_membership = (TextView) itemView.findViewById(R.id.user_membership);
            user_status = (ImageView) itemView.findViewById(R.id.user_status);
            updateBtn = itemView.findViewById(R.id.update_btn);
            editNotes = itemView.findViewById(R.id.edit_notes);
            notesSection = itemView.findViewById(R.id.notes_section);
            userImage = itemView.findViewById(R.id.user_image);


        }
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            imageIbUpdateDialog.setImageBitmap(imageBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            mData = baos.toByteArray();

        }
    }

}


