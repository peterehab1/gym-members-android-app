package com.example.peter.basic_app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.DatePicker;
import android.widget.EditText;
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
import static com.example.peter.basic_app.HomeActivity.getMembershipFromFirebaseDatabase;
import static com.example.peter.basic_app.HomeActivity.setMembershipForFirebaseDatabase;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

    List<Users> listdata;
    DatabaseReference mRef;
    Object checkedItem;
    String theFinalDate;
    DatePickerDialog.OnDateSetListener chooseDate;



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
        if (data.getLeftmoney().matches("0")){
            myViewHolder.user_membership.setText(" نظام الأشتراك : " + getMembershipFromFirebaseDatabase(data.getMembership()));
        }else{
            myViewHolder.user_membership.setText(" نظام الأشتراك : " + getMembershipFromFirebaseDatabase(data.getMembership()) + " - متبقي : " + String.valueOf(data.getLeftmoney()) + " جنيهاً ");
        }


        String startDate = data.getStartdate();
        String todayDate = dateFormatter(System.currentTimeMillis(), "MM/dd/yyyy");

        Date date1 = new Date(startDate); //  Month/Day/Year
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

        myViewHolder.payLeftmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (data.getLeftmoney().matches("0")){
                    Toast.makeText(v.getContext(),  "لا يوجد باقي", Toast.LENGTH_SHORT).show();
                }else{

                    final Context context = v.getContext();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

                    builder.setMessage(" تأكيد دفع العضو " + data.getName() + " لمبلغ " + data.getLeftmoney() + " جنيهاً ؟ ");
                    builder.setTitle(" دفع باقي أشتراك ");

                    builder.setPositiveButton("تجديد الأشتراك", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(data.getKey());
                            Map<String,Object> taskMap = new HashMap<String,Object>();
                            taskMap.put("leftmoney", "0");
                            mRef.updateChildren(taskMap);
                            Toast.makeText(context,  " تم دفع المتبقي ", Toast.LENGTH_LONG).show();
                        }
                    });

                    builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            }
        });


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

                            //final TextView dateStartUpdate = mainView.findViewById(R.id.date_start_update);

                            //CalendarView calendarView = (CalendarView) mainView.findViewById(R.id.calendarView2);
                            EditText membershipLeftmoney = (EditText) mainView.findViewById(R.id.membership_left_money);

                            if (membershipLeftmoney.getText().toString().isEmpty()){
                                taskMap.put("leftmoney", "0");
                            }else{
                                taskMap.put("leftmoney", membershipLeftmoney.getText().toString());
                            }

                            taskMap.put("membership", setMembershipForFirebaseDatabase(checkedItem.toString()));

                            Calendar cal = Calendar.getInstance();
                            int year = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH);
                            month = month + 1;
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            theFinalDate = month + "/" + day + "/" + year;
                            taskMap.put("startdate", theFinalDate);
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
        Button payLeftmoney;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_membership = (TextView) itemView.findViewById(R.id.user_membership);
            user_status = (ImageView) itemView.findViewById(R.id.user_status);
            updateBtn = itemView.findViewById(R.id.update_btn);
            payLeftmoney = itemView.findViewById(R.id.pay_left);

        }
    }

}
