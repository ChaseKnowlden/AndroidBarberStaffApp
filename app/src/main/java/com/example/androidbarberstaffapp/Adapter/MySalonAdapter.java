package com.example.androidbarberstaffapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidbarberstaffapp.Common.Common;
import com.example.androidbarberstaffapp.Common.CustomLoginDialog;
import com.example.androidbarberstaffapp.Interface.IDialogClickListener;
import com.example.androidbarberstaffapp.Interface.IGetBarberListener;
import com.example.androidbarberstaffapp.Interface.IRecyclerItemSelectedListener;
import com.example.androidbarberstaffapp.Interface.IUserLoginRememberListener;
import com.example.androidbarberstaffapp.Model.Barber;
import com.example.androidbarberstaffapp.Model.Salon;
import com.example.androidbarberstaffapp.R;
import com.example.androidbarberstaffapp.StaffHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.MyViewHolder> implements IDialogClickListener {

    Context context;
    List<Salon> salonList;
    List<CardView> cardViewList;

    IUserLoginRememberListener iUserLoginRememberListener;
    IGetBarberListener iGetBarberListener;

    public MySalonAdapter(Context context, List<Salon> salonList, IUserLoginRememberListener iUserLoginRememberListener,IGetBarberListener iGetBarberListener) {
        this.context = context;
        this.salonList = salonList;
        cardViewList = new ArrayList<>();
        this.iUserLoginRememberListener = iUserLoginRememberListener;
        this.iGetBarberListener = iGetBarberListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_salon,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_salon_name.setText(salonList.get(i).getName());
        myViewHolder.txt_salon_address.setText(salonList.get(i).getAddress());
        if (!cardViewList.contains(myViewHolder.card_salon))
            cardViewList.add(myViewHolder.card_salon);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {
                Common.selected_salon = salonList.get(position);
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {
        CustomLoginDialog.getInstance()
                .showLoginDialog("STAFF LOGIN",
                        "LOGIN",
                        "CANCEL",
                        context,
                        this);
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    @Override
    public void onClickPositiveButton(final DialogInterface dialogInterface, final String userName, String password) {
        final AlertDialog loading = new SpotsDialog.Builder().setCancelable(false)
                .setContext(context).build();
        loading.show();

        FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selected_salon.getSalonId())
                .collection("Barbers")
                .whereEqualTo("username",userName)
                .whereEqualTo("password",password)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size() > 0)
                    {
                        dialogInterface.dismiss();
                        loading.dismiss();
                        iUserLoginRememberListener.onUserLoginSuccess(userName);

                        Barber barber = new Barber();
                        for (DocumentSnapshot barberSnapShot:task.getResult())
                        {
                            barber = barberSnapShot.toObject(Barber.class);
                            barber.setBarberId(barberSnapShot.getId());
                        }

                        iGetBarberListener.onGetBarberSuccess(barber);

                        Intent staffHome = new Intent(context, StaffHomeActivity.class);
                        staffHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        staffHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(staffHome);
                    }
                    else
                    {
                        loading.dismiss();
                        Toast.makeText(context, "Wrong username / password or wrong salon", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_salon_name,txt_salon_address;
        CardView card_salon;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_salon = (CardView)itemView.findViewById(R.id.card_salon);
            txt_salon_address = (TextView)itemView.findViewById(R.id.txt_salon_address);
            txt_salon_name = (TextView)itemView.findViewById(R.id.txt_salon_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelected(v,getAdapterPosition());
        }
    }
}
