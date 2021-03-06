package com.example.unifiedshopping;

import static java.util.Objects.isNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.example.unifiedshopping.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements AdapterOrders.ItemClickListner,AdapterOrdersHidden.ItemClickListner,AdapterOrdersDismissed.ItemClickListner, AdapterVendor.ItemClickListner{

    private ArrayList<Order> test;
    private RecyclerView mainView;
    public RecyclerView.Adapter myAdapter;
    public LinearLayoutManager listlayoutManager;
    public MyViewModel dataholder;
    public TextView Header;
    public ImageButton homeBtn;
    public ImageButton completedBtn;

    public int curr_screen_flag = 0;  //0-home,1-hidden,2-dismissed,3-delivered

    public ArrayList<Vendor> vendorArrayList = new ArrayList<>();
    //public ArrayList<Boolean> switchState = new ArrayList<>();

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        dataholder = new ViewModelProvider(this).get(MyViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Header = findViewById(R.id.textViewMain);

        //initSwitchState();
        dataholder.initSwitchState(vendorArrayList);

        createVendorsList();
        //centerTitle();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            //Toast.makeText(this, "hi", Toast.LENGTH_LONG).show();
            Header.setVisibility(View.VISIBLE);
            switch (item.getItemId()) {
                case R.id.home:
                    setAdapter(dataholder.getMainList(),1);
                    Header.setText("  Your Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                    curr_screen_flag = 0;
                    break;
                case R.id.vendor:
                    //Toast.makeText(this, "vendor", Toast.LENGTH_SHORT).show();
                    setVendorAdapter(vendorArrayList, 1);
                    Header.setText("  Vendors "+"("+Integer.toString(myAdapter.getItemCount())+")");
                    curr_screen_flag = 10;
                    break;
                case R.id.history:
                    setAdapter(dataholder.getDeliveredList(),4);
                    Header.setText("  Completed Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                    curr_screen_flag = 3;
                    break;
            }
            return true;
        });

        mainView = binding.recyclerViewMain;

        setTop10();
        dataholder.init(test);
        setAdapter(dataholder.getMainList(),1);
        Header.setText("  Your Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");

    }
    //center the title
    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Header.setVisibility(View.VISIBLE);
        switch(item.getItemId())
        {

            case R.id.show_hidden:
            {
                setAdapter(dataholder.getHiddenList(),0);
                Header.setText("  Hidden Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                curr_screen_flag = 1;
            }
            return true;

            case R.id.show_dismissed:
            {
                setAdapter(dataholder.getDismissedList(), -1);
                Header.setText("  Dismissed Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                curr_screen_flag = 2;
            }
            return true;

            case R.id.setting:
            {
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            }

            default:return super.onOptionsItemSelected(item);
        }
    }

    public void setVendorAdapter(ArrayList<Vendor> v, int vc) {
        listlayoutManager = new LinearLayoutManager(this);
        mainView.setLayoutManager(listlayoutManager);
        mainView.setItemAnimator(new DefaultItemAnimator());

        if (vc == 1) {
            myAdapter = new AdapterVendor(v,this, this);
        }

        mainView.setAdapter(myAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mainView.getContext(),listlayoutManager.getOrientation());
        mainView.addItemDecoration(divider);
    }

    /*public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.toggle2:
                //Toast.makeText(this, "hi", Toast.LENGTH_LONG).show();
                v.
                break;
        }
    }*/

    public void setAdapter(ArrayList<Order> X, int view_code){
        listlayoutManager = new LinearLayoutManager(this);
        mainView.setLayoutManager(listlayoutManager);
        mainView.setItemAnimator(new DefaultItemAnimator());
        Log.i("yash inside set adapter", Integer.toString(test.size()));
        if (view_code == 1)
            myAdapter = new AdapterOrders(X,this,this);
        else if (view_code == 0)
            myAdapter = new AdapterOrdersHidden(X,this,this);
        else if (view_code == -1)
            myAdapter = new AdapterOrdersDismissed(X,this,this);
        else if (view_code == 4)
            myAdapter = new AdapterOrdersCompleted(X,this);

        mainView.setAdapter(myAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mainView.getContext(),listlayoutManager.getOrientation());
        mainView.addItemDecoration(divider);

    }

    //Launch Order Detail activity
    @Override
    public void onItemClick(int pos) {
        Header.setVisibility(View.INVISIBLE);
        //Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_SHORT);
        Order t = null;
        switch (curr_screen_flag)
        {
            case 0:
                t = dataholder.getMainList().get(pos);
                break;
            case 1:
                t = dataholder.getHiddenList().get(pos);
                break;
            case 2:
                t = dataholder.getDismissedList().get(pos);
                break;
            case 3:
                t = dataholder.getDeliveredList().get(pos);
                break;
            default:
                return;
        }

        ArrayList<Order> orderArrayList = new ArrayList<>();
        orderArrayList.add(t);

        listlayoutManager = new LinearLayoutManager(this);
        mainView.setLayoutManager(listlayoutManager);
        mainView.setItemAnimator(new DefaultItemAnimator());

        myAdapter = new OrderDetailsAdapter(orderArrayList,this);

        mainView.setAdapter(myAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mainView.getContext(),listlayoutManager.getOrientation());
        mainView.addItemDecoration(divider);


    }

    public void createVendorsList() {
        ArrayList<Vendor> vendors = new ArrayList<>();
        Vendor v1 = new Vendor("Amazon", new Switch(getApplicationContext()));
        Vendor v2 = new Vendor("Costco", new Switch(getApplicationContext()));
        Vendor v3 = new Vendor("Nike", new Switch(getApplicationContext()));
        Vendor v4 = new Vendor("Walmart", new Switch(getApplicationContext()));

        vendors.add(v1);
        vendors.add(v2);
        vendors.add(v3);
        vendors.add(v4);

        vendorArrayList = vendors;

    }

    public void setTop10()
    {
        test = new ArrayList<Order>();
        Order o1 = new Order("Bnesi Personalized Photo Color Film Customization Personalized Gift Keychain, Customized UniqueMeaning Gift Customization For Family, Friends", "Delivered on March 4", "64 Modoc Alley", "String datePlaced", R.drawable.picturekeychain , "Amazon", 16.99);
        Order o2 = new Order("Bed Sheets", "Shipped on April 9", "3469 Cecil St", "Placed on April 8", R.drawable.bed_sheets , "Amazon", 49.99);
        Order o3 = new Order("Dinnerware", "In transit", "3469 Cecil St", "Placed on April 10", R.drawable.dinnerware , "Amazon", 99.99);
        Order o4 = new Order("Cheese, Ramen and more", "Shipped on April 12", "3469 Cecil St", "Placed on April 8", R.drawable.chees_ramen , "Walmart", 4.99);
        Order o5 = new Order("Green Tea", "Shipped on April 10", "64 Modoc Alley", "Placed on April 12", R.drawable.green_tea , "Walmart", 3.99);
        Order o6 = new Order("Light Saber", "In transit", "3469 Cecil St", "Placed on March 22", R.drawable.lightsaber , "Amazon", 421.95);
        Order o7 = new Order("Salt and Pepper", "In transit", "64 Modoc Alley", "Placed on April 1", R.drawable.salt_pepper , "Walmart", 9.99);
        test.add(o1);
        test.add(o2);
        test.add(o3);
        test.add(o5);
        test.add(o6);
        test.add(o7);
        test.add(o4);
        /*for (int i=0 ; i<test.size() ; i++)
        {
            Log.i("Yash", test.get(i).getOrderName()+" "+test.get(i).getPriorityFlag() );
        }*/
        Collections.sort(test);
        /*for (int i=0 ; i<test.size() ; i++)
        {
            Log.i("Yash", test.get(i).getOrderName()+" "+test.get(i).getPriorityFlag() );
        }*/

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        int clkpos = item.getGroupId();
        Order t;// = dataholder.getMainList().get(clkpos);
        //Log.i("Yash", "clkpos: "+Integer.toString(clkpos)+" "+Integer.toString(item.getItemId()));
        switch(item.getItemId())
        {

            case 666:
                t = dataholder.getMainList().get(clkpos);
                if (t.getPriorityFlag()==1)
                {
                    Toast.makeText(this,"Notice: Order Priority is already set to High",Toast.LENGTH_LONG).show();
                    return true;
                }
                else {
                    //Log.i("Yash", "onContextItemSelected: "+Integer.toString(dataholder.getMainList().get(clkpos).getPriorityFlag()));
                    t.setPriorityFlag(1);
                    //Log.i("Yash", "onContextItemSelected: "+Integer.toString(dataholder.getMainList().get(clkpos).getPriorityFlag()));
                    Header.setText("  Your Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                    Toast.makeText(this,"Priority set to High!",Toast.LENGTH_LONG).show();
                    dataholder.sortMain();
                    //listlayoutManager.scrollToPositionWithOffset(0,0);
                    mainView.smoothScrollToPosition(0);
                    //listlayoutManager.smoothScrollToPosition(mainView, new RecyclerView.State(), 0);
                }
                break;
            case 667:
                t = dataholder.getMainList().get(clkpos);
                if (t.getPriorityFlag()==0)
                {
                    Toast.makeText(this,"Notice: Order Priority is already set to Default",Toast.LENGTH_LONG).show();
                    return true;
                }
                else {
                    t.setPriorityFlag(0);
                    Toast.makeText(this,"Priority set to Default!",Toast.LENGTH_LONG).show();
                    Header.setText("  Your Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                    dataholder.sortMain();
                    mainView.smoothScrollToPosition(dataholder.getMainList().indexOf(t));
                }
                break;
            case 668:
                t = dataholder.getMainList().get(clkpos);
                if (t.getPriorityFlag()==-1)
                {
                    Toast.makeText(this,"Notice: Order Priority is already set to Low",Toast.LENGTH_LONG).show();
                    return true;
                }
                else {
                    t.setPriorityFlag(-1);
                    Toast.makeText(this,"Priority set to Low!",Toast.LENGTH_LONG).show();
                    Header.setText("  Your Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                    dataholder.sortMain();
                    mainView.smoothScrollToPosition(dataholder.getMainList().indexOf(t));
                }
                break;
            case 669:
                t = dataholder.getMainList().get(clkpos);
                t.setVisibility(0);
                dataholder.hideOrder(clkpos);
                Toast.makeText(this,"Order is now hidden!",Toast.LENGTH_LONG).show();
                Header.setText("  Your Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                break;
            case 670:
                t = dataholder.getMainList().get(clkpos);
                t.setVisibility(-1);
                dataholder.stopTrackingOrder(clkpos);
                Toast.makeText(this,"Order dismissed and will not be tracked anymore!",Toast.LENGTH_LONG).show();
                Header.setText("  Your Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                break;
            case 766:
                t = dataholder.getHiddenList().get(clkpos);
                t.setVisibility(1);
                dataholder.unHideOrder(clkpos);
                Toast.makeText(this,"Order now visible on homepage!",Toast.LENGTH_LONG).show();
                Header.setText("  Hidden Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                break;
            case 870:
                t = dataholder.getHiddenList().get(clkpos);
                t.setVisibility(1);
                dataholder.stopTrackingHidden(clkpos);
                Toast.makeText(this,"Order dismissed and will not be tracked anymore",Toast.LENGTH_LONG).show();
                Header.setText("  Hidden Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                break;
            case 866:
                t = dataholder.getDismissedList().get(clkpos);
                t.setVisibility(1);
                dataholder.trackOrder(clkpos);
                Toast.makeText(this,"Order is now being tracked!",Toast.LENGTH_LONG).show();
                Header.setText("  Dismissed Orders "+"("+Integer.toString(myAdapter.getItemCount())+")");
                break;


            default:  return super.onContextItemSelected(item);
        }
        myAdapter.notifyDataSetChanged();
        return true;
    }


}