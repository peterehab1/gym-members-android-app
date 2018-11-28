package com.example.peter.basic_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.peter.basic_app.AddActivity.*;
import static com.example.peter.basic_app.HomeActivity.check;
import static com.example.peter.basic_app.HomeActivity.dateFormatter;
import static com.example.peter.basic_app.HomeActivity.getDateDiff;
import static com.example.peter.basic_app.HomeActivity.setMembershipForFirebaseDatabase;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

    List<Users> listdata;
    DatabaseReference mRef;
    Object checkedItem;



    public RecyclerviewAdapter(List<Users> listdata) {

        this.listdata = listdata;

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
        myViewHolder.user_membership.setText(data.getMembership());



        String startDate = data.getStartdate();
        String todayDate = dateFormatter(System.currentTimeMillis(), "MM/dd/yyyy");

        Date date1 = new Date(startDate); //  Month/Day/Year
        Date date2 = new Date(todayDate);

        long diff = getDateDiff(date1, date2);
        myViewHolder.user_status.setImageResource(check(diff, data.getMembership()));


        myViewHolder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );


                String[] membershipTypes = new String[] {
                        "نصف  شهر","1 شهر", "3 أشهر", "6 أشهر", "عام واحد"
                };

                final View mainView = inflater.inflate(R.layout.update_dialog, null);
                builder.setView(mainView);
                builder.setTitle("تجديد أشتراك " + data.getName());
                checkedItem = "1 شهر";
                builder.setSingleChoiceItems(membershipTypes, 1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int n) {
                        // ...
                        ListView lw = ((AlertDialog)d).getListView();
                        checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());

                    }



                });

                builder.setPositiveButton("تجديد الأشتراك", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (checkedItem != null){
                            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(data.getKey());
                            Map<String,Object> taskMap = new HashMap<String,Object>();

                            CalendarView calendarView = (CalendarView) mainView.findViewById(R.id.calendarView2);

                            taskMap.put("membership", setMembershipForFirebaseDatabase(checkedItem.toString()));
                            taskMap.put("startdate", dateFormatter(calendarView.getDate(), "MM/dd/yyyy"));
                            mRef.updateChildren(taskMap);
                            Toast.makeText(context,  " تم تجديد أشتراك "+data.getName()+" لمدة "+checkedItem.toString(), Toast.LENGTH_LONG).show();

                        }else{

                            Toast.makeText(context,  "لم يتم تحديد نظام الأشتراك", Toast.LENGTH_LONG).show();

                        }

                    }
                });

                builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

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


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_membership = (TextView) itemView.findViewById(R.id.user_membership);
            user_status = (ImageView) itemView.findViewById(R.id.user_status);
            updateBtn = itemView.findViewById(R.id.update_btn);

        }
    }

}
