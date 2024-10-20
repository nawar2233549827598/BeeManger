package com.example.fianlebee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BudgetActivity extends AppCompatActivity {
    private TextView totalBudgetAmountTextView;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private String post_key ="";
    private String item = "";
    private int amount =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
       // personalRef = FirebaseDatabase.getInstance().getReference("personal").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);
        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount=0;
                for (DataSnapshot snap: snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalAmount += data.getAmount();
                    String sTotal = String.valueOf("Month budget: $"+ totalAmount);
                    totalBudgetAmountTextView.setText(sTotal);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additem();
            }
        });
    }

    private void additem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout,null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.itemsspinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String budgetAmount = amount.getText().toString();
                String budgtItem = itemSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Amount is required!");
                    return;
                }

                if (budgtItem.equals("Select item")){
                    Toast.makeText(BudgetActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setMessage("adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id  = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);

                    String itemNday = budgtItem+date;
                    String itemNweek = budgtItem+weeks.getWeeks();
                    String itemNmonth = budgtItem+months.getMonths();

                    Data data = new Data(budgtItem, date, id, itemNday, itemNweek, itemNmonth, Integer.parseInt(budgetAmount), weeks.getWeeks(), months.getMonths(), null);

                    budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this,"Budget item added succefully!!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();
                        }
                    });
                }
dialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(budgetRef, Data.class)
                        .build();
        FirebaseRecyclerAdapter<Data,myViewHolder> adapter = new FirebaseRecyclerAdapter<Data, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setItemAmount("Allocated amount: $" + model.getAmount());
                holder.setDate("On: " + model.getDate());
                holder.setItemName("BudgetItem: " + model.getItem());

                holder.notes.setVisibility(View.GONE);

                switch (model.getItem()) {
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.transport);
                        break;
                    case "Food":
                        holder.imageView.setImageResource(R.drawable.food);
                        break;
                    case "House":
                        holder.imageView.setImageResource(R.drawable.house);
                        break;
                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.entertainment);
                        break;
                    case "Education":
                        holder.imageView.setImageResource(R.drawable.education);
                        break;
                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.consultancy);
                        break;
                    case "Apparel":
                        holder.imageView.setImageResource(R.drawable.shirt);
                        break;
                    case "Health":
                        holder.imageView.setImageResource(R.drawable.health);
                        break;
                    case "Personal":
                        holder.imageView.setImageResource(R.drawable.profile);
                        break;
                    case "Other":
                        holder.imageView.setImageResource(R.drawable.other);
                        break;
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        updateData();
                    }
                });


            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new myViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();


    }

    public void onClick(View view) {
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ImageView imageView;
        public TextView notes;

        public myViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            notes = itemView.findViewById(R.id.note);

        }

        public void setItemName(String itemName) {
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount) {
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }

        public void setDate(String itemDate) {
            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate); }


    }

    private void updateData (){

            AlertDialog.Builder myDialog= new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View mView = inflater.inflate(R.layout.update_layout, null);

            myDialog.setView(mView);
            final  AlertDialog dialog = myDialog.create();

            final TextView mItem = mView.findViewById(R.id.itemName);
            final EditText mAmount = mView.findViewById(R.id.amount);
            final  EditText mNotes = mView.findViewById(R.id.note);

            mNotes.setVisibility(View.GONE);

            mItem.setText(item);

            mAmount.setText(String.valueOf(amount));
            mAmount.setSelection(String.valueOf(amount).length());

            Button delBut = mView.findViewById(R.id.btnDelete);
            Button btnUpdate = mView.findViewById(R.id.btnUpdate);

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    amount = Integer.parseInt(mAmount.toString());

                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);

                    Data data = new Data(item, date, post_key, null, amount, weeks.getWeeks(), months.getMonths());
                    budgetRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this,"updated succeffuly!!",Toast.LENGTH_SHORT).show(); }
                                else{
                                    Toast.makeText(BudgetActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.show();

                }
            });

            delBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this,"Deleted succeffuly!!",Toast.LENGTH_SHORT).show(); }
                            else{
                                Toast.makeText(BudgetActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.dismiss();

                }
            });
            dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.account){
            Intent i = new Intent(BudgetActivity.this,LogIn.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
