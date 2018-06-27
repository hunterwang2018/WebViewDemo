package com.hunter.tool.webviewdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hunter.tool.webviewdemo.database.SearchInstance;
import com.hunter.tool.webviewdemo.util.FileUtil;
import com.hunter.tool.webviewdemo.adapter.ConditionVO;
import com.hunter.tool.webviewdemo.adapter.ModelAdapter;
import com.hunter.tool.webviewdemo.adapter.ModelBase;
import com.hunter.tool.webviewdemo.adapter.SearchVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/30 0030.
 */

public class SettingActivity extends Activity implements View.OnClickListener
        , ModelAdapter.OnRecyclerViewListener , ModelAdapter.SaveEditListener{
    private final static String TAG = "SettingActivity";
    RecyclerView recyclerView;
//    SearchAdapter mAdapter;
//    ArrayList<String> mSearch = new ArrayList<String>();

    ModelAdapter mAdapter;
    ArrayList<ModelBase> mData = new ArrayList<ModelBase>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_search);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mData = readDataInit(this);
        mAdapter = new ModelAdapter(this, mData);

        mAdapter.setOnRecyclerViewListener(this);
        mAdapter.setOnSaveEditListener(this);
        recyclerView.setAdapter(mAdapter);

        Button btnAdd = (Button)findViewById(R.id.btnAddSearch);
        btnAdd.setOnClickListener(this);

//        Button btnTest = (Button)findViewById(R.id.btnTest);
//        btnTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               save();
//            }
//        });
//
//        Button btnRead = (Button)findViewById(R.id.btnRead);
//        btnRead.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        btnRead.setVisibility(View.INVISIBLE);

        if (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    ,1);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        save();
    }

    @NonNull
    public static ArrayList<ModelBase> readDataInit(Context context) {
        ArrayList<ModelBase> arrData = new ArrayList<ModelBase>();
        String data = FileUtil.readerFile(context);

        String[] data1 = data.split("\\*");
        ArrayList<SearchInstance> arr = doData(data1);

        for(SearchInstance si : arr) {
            String search = si.getSearch();
            SearchVO sv = new SearchVO();
            sv.setEtSearch(search);
            arrData.add(sv);
            ArrayList<ConditionVO> cvo = si.getArrConditions();
            for(ConditionVO cv : cvo) {
                arrData.add(cv);
            }
        }

        return arrData;
    }

    public static ArrayList<SearchInstance> readDataInstance(Context context) {
        ArrayList<ModelBase> arrData = new ArrayList<ModelBase>();
        String data = FileUtil.readerFile(context);

        String[] data1 = data.split("\\*");
        ArrayList<SearchInstance> arr = doData(data1);
        return arr;
    }



    int count = 0;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddSearch:
                count++;
                SearchVO s = new SearchVO();
                s.setEtSearch("");
                addSearchItem(s);
                break;

        }
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Item = " + position, Toast.LENGTH_LONG).show();


        ModelBase mb = mData.get(position);
        if(mb instanceof SearchVO) {
            //新增Condition
            ConditionVO c = new ConditionVO();
            c.setEtCondition("");
            c.setEtKick("");
            c.setEtPrice("");

            int item = position + 1;
            mData.add(item, c);
            mAdapter.notifyItemChanged(item);
            mAdapter.notifyItemInserted(item);
            mAdapter.notifyItemRangeChanged(item, mAdapter.getItemCount());
        } else if(mb instanceof ConditionVO) {
            //删除Condition
            mAdapter.notifyItemChanged(position);
            mData.remove(position);
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
        }

    }

    @Override
    public void onSearchItemClickDel(int position) {
        //Toast.makeText(this, "Del Item = " + position, Toast.LENGTH_SHORT).show();

        ModelBase mb = mData.get(position);
        if(position+1 == mData.size()) {
            //do del
            //Toast.makeText(this, "do del = " + position, Toast.LENGTH_SHORT).show();
            mAdapter.notifyItemChanged(position);
            mData.remove(position);
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            return;
        }

        if(mData.get(position+1) instanceof ConditionVO) {
            //do not del
            //Toast.makeText(this, "do not del = " + position, Toast.LENGTH_SHORT).show();
            return;
        } else if(mData.get(position+1) instanceof SearchVO) {
            mAdapter.notifyItemChanged(position);
            mData.remove(position);
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
        }



    }

    private void addSearchItem(SearchVO svo) {
        int last = mData.size() +1;
        mData.add(svo);
        mAdapter.notifyItemChanged(last);
        mAdapter.notifyItemInserted(last);
        mAdapter.notifyItemRangeChanged(last, mAdapter.getItemCount());
    }

    @Override
    public void SaveSearchEdit(int position, String string) {
        Log.i(TAG, "position=" + position + " string=" + string);
        ((SearchVO)mData.get(position)).setEtSearch(string);
    }

    @Override
    public void SaveConditionEdit(int position, String string) {
        Log.i(TAG, "position=" + position + " string=" + string);
        ((ConditionVO)mData.get(position)).setEtCondition(string);
    }

    @Override
    public void SaveKickEdit(int position, String string) {
        Log.i(TAG, "position=" + position + " string=" + string);
        ((ConditionVO)mData.get(position)).setEtKick(string);
    }

    @Override
    public void SavePriceEdit(int position, String string) {
        Log.i(TAG, "position=" + position + " string=" + string);
        ((ConditionVO)mData.get(position)).setEtPrice(string);
    }

    private void save() {
        FileUtil.writeFileInit(this);

        StringBuffer sb = new StringBuffer();

        for(ModelBase mb : mData) {
            if(mb instanceof SearchVO) {
                SearchVO svo = (SearchVO)mb;
                String search = svo.getEtSearch();

                sb.append("TYPE_SEARCH#");
                sb.append(search+"#");
            } else if(mb instanceof ConditionVO) {
                ConditionVO cvo = (ConditionVO)mb;
                String condition = cvo.getEtCondition();
                String kick = cvo.getEtKick();
                String price = cvo.getEtPrice();

                sb.append("TYPE_CONDITION#");
                sb.append(condition+"#");
                sb.append(kick+"#");
                sb.append(price+"#");
            }

            FileUtil.writeFile(this, sb.toString());
            sb.delete(0, sb.length());
        }
    }

    private static ArrayList<SearchInstance> doData(String[] data) {
        SearchInstance si = new SearchInstance();
        ArrayList<SearchInstance> arr = new ArrayList<SearchInstance>();

        int item = 0;
        for(String value : data) {
            String[] data1 = value.split("#");

            if(data1[0].equals("TYPE_SEARCH")) {
                String search = data1[1];
                si = new SearchInstance();
                si.setSearch(search);

                arr.add(si);
                item = arr.size()-1;
            } else if(data1[0].equals("TYPE_CONDITION")) {
                String condition = data1[1];
                String kick = data1[2];
                String price = data1[3];

                ConditionVO cv = new ConditionVO();
                cv.setEtCondition(condition);
                cv.setEtKick(kick);
                cv.setEtPrice(price);

                arr.get(item).getArrConditions().add(cv);
            }
        }

        Log.i(TAG, "arr size = " + arr.size());
        return arr;
    }



}
