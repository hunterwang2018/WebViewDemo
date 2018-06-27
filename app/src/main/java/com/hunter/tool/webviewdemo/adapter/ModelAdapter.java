package com.hunter.tool.webviewdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hunter.tool.webviewdemo.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/4 0004.
 */

public class ModelAdapter extends RecyclerView.Adapter {
    private final static String TAG = "ModelAdapter";

    SaveEditListener mSaveEditListener;
    public interface SaveEditListener {
        void SaveSearchEdit(int position, String string);
        void SaveConditionEdit(int position, String string);
        void SaveKickEdit(int position, String string);
        void SavePriceEdit(int position, String string);
    }

    public void setOnSaveEditListener(SaveEditListener onSaveEditListener) {
        this.mSaveEditListener = onSaveEditListener;
    }

    public static enum ITEM_TYPE {
        TYPE_SERACH,
        TYPE_CONDITION
    }

    public interface OnRecyclerViewListener {
        void onItemClick(int position);
        void onSearchItemClickDel(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private LayoutInflater mInflater;
    ArrayList<ModelBase> mData = new ArrayList<ModelBase>();
    public ModelAdapter(Context context, ArrayList<ModelBase> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
    }

    @Override
    public int getItemViewType(int position) {
        ModelBase mb =  mData.get(position);
        if(mb instanceof SearchVO) {
            return ITEM_TYPE.TYPE_SERACH.ordinal();
        } else if(mb instanceof ConditionVO) {
            return ITEM_TYPE.TYPE_CONDITION.ordinal();
        }
        return position % 2 == 0 ? ITEM_TYPE.TYPE_SERACH.ordinal() : ITEM_TYPE.TYPE_CONDITION.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.TYPE_SERACH.ordinal()) {
            View view =  mInflater.inflate(R.layout.setting_search, null);
            return new SearchHolder(view);
        } else if (viewType == ITEM_TYPE.TYPE_CONDITION.ordinal()) {
            View view =  mInflater.inflate(R.layout.condition_item, null);
            return new ConditionHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SearchHolder) {
            SearchHolder sholder = (SearchHolder)holder;
            sholder.position = position;
            String search = ((SearchVO) mData.get(position)).getEtSearch();
            sholder.etSearch.setText(search);
        } else if(holder instanceof ConditionHolder) {
            ConditionHolder cholder = (ConditionHolder)holder;
            cholder.position = position;
            String condition = ((ConditionVO)mData.get(position)).getEtCondition();
            cholder.etCondition.setText(condition);
            String kick = ((ConditionVO)mData.get(position)).getEtKick();
            cholder.etKick.setText(kick);
            String price = ((ConditionVO) mData.get(position)).getEtPrice();
            cholder.etPrice.setText(price);
        }
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ConditionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        public final static int TYPE_CONDITION = 0x01;
//        public final static int TYPE_KICK = 0x02;
//        public final static int TYPE_PRICE = 0x03;

        public int position;
//        public int type;
        public EditText etCondition;
        public EditText etKick;
        public EditText etPrice;
        public Button btnDel;

        public ConditionHolder(View itemView) {
            super(itemView);

            etCondition = (EditText)itemView.findViewById(R.id.etCondition);
            etCondition.addTextChangedListener(new ConditionTextSwitcher(this));
            etKick = (EditText)itemView.findViewById(R.id.etConditionKick);
            etKick.addTextChangedListener(new KickTextSwitcher(this));
            etPrice = (EditText)itemView.findViewById(R.id.etPrice);
            etPrice.addTextChangedListener(new PriceTextSwitcher(this));
            btnDel = (Button)itemView.findViewById(R.id.btnDelCondition);
            btnDel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }
        }

        class PriceTextSwitcher implements TextWatcher {
            private ConditionHolder mHolder;

            public PriceTextSwitcher(ConditionHolder mHolder) {
                this.mHolder = mHolder;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != mSaveEditListener) {
                    mSaveEditListener.SavePriceEdit(mHolder.position, s.toString());
                }
            }
        }

        class KickTextSwitcher implements TextWatcher {
            private ConditionHolder mHolder;

            public KickTextSwitcher(ConditionHolder mHolder) {
                this.mHolder = mHolder;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != mSaveEditListener) {
                    mSaveEditListener.SaveKickEdit(mHolder.position, s.toString());
                }
            }
        }

        //自定义EditText的监听类
        class ConditionTextSwitcher implements TextWatcher {

            private ConditionHolder mHolder;

            public ConditionTextSwitcher(ConditionHolder mHolder) {
                this.mHolder = mHolder;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "afterTextChanged");
                //用户输入完毕后，处理输入数据，回调给主界面处理
                if (null != mSaveEditListener) {
                    mSaveEditListener.SaveConditionEdit(mHolder.position, s.toString());
                }
            }
        }
    }

    class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public int position;
        public EditText etSearch;
        public Button btnDelSearch;
        public Button btnSearch;

        public SearchHolder(View itemView) {
            super(itemView);

            etSearch = (EditText)itemView.findViewById(R.id.etSearch);
            etSearch.addTextChangedListener(new SearchTextSwitcher(this));
            btnSearch = (Button)itemView.findViewById(R.id.btnSearchDesc);
            btnSearch.setOnClickListener(this);
            btnDelSearch = (Button)itemView.findViewById(R.id.btnDelSearch);
            btnDelSearch.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != onRecyclerViewListener) {
                if(view.getId() == R.id.btnDelSearch) {
                    onRecyclerViewListener.onSearchItemClickDel(position);
                    return;
                }

                onRecyclerViewListener.onItemClick(position);
            }
        }

        class SearchTextSwitcher implements TextWatcher {
            private SearchHolder mHolder;

            public SearchTextSwitcher(SearchHolder mHolder) {
                this.mHolder = mHolder;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != mSaveEditListener) {
                    mSaveEditListener.SaveSearchEdit(mHolder.position, s.toString());
                }
            }
        }
    }
}
