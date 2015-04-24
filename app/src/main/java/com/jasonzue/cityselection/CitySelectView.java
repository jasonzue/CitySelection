package com.jasonzue.cityselection;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 城市选择自定义View
 * Created by Jason Zue on 2015/4/1.
 */
public class CitySelectView extends FrameLayout {


    private ListView mProvienceLv, mCityLv, mAreaLv;
    private List<String> provienceData = new ArrayList<>();
    private List<String> cityData = new ArrayList<>();
    private List<String> areaData = new ArrayList<>();

    private CommonAdapter mProvienceAdapter, mCityAdapter, mAreaAdapter;

    private String provience, city, area;
    private String json = getFromAssets("address.js");


    public void setOnResultSelectListener(OnResultSelectListener onResultSelectListener) {
        this.onResultSelectListener = onResultSelectListener;
    }

    private OnResultSelectListener onResultSelectListener;

    public CitySelectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    private void initView(Context context) {
        View layout = LayoutInflater.from(context).inflate(
                R.layout.city_selete_view, this, true);
        mProvienceLv = (ListView) layout.findViewById(R.id.first_lv);
        mCityLv = (ListView) layout.findViewById(R.id.second_lv);
        mAreaLv = (ListView) layout.findViewById(R.id.third_lv);
        mProvienceAdapter = new CommonAdapter(context, provienceData);
        mCityAdapter = new CommonAdapter(context, cityData);
        mAreaAdapter = new CommonAdapter(context, areaData);

        mProvienceLv.setAdapter(mProvienceAdapter);
        mCityLv.setAdapter(mCityAdapter);
        mAreaLv.setAdapter(mAreaAdapter);

        mProvienceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                provience = provienceData.get(position);
                //刷新市列表
                mAreaAdapter.setSelectedPosition(-1);
                mCityAdapter.setSelectedPosition(-1);
                mProvienceAdapter.setSelectedPosition(position);
                view.setBackgroundColor(getResources().getColor(R.color.common_bg_blue_color));
                cityData.clear();
                areaData.clear();
                GetCityData(provience);
                if (onResultSelectListener != null) {
                    onResultSelectListener.onResultSelected(false, provience, null, null);
                }
                mProvienceAdapter.notifyDataSetInvalidated();

            }
        });
        mCityLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = cityData.get(position);
                mAreaAdapter.setSelectedPosition(-1);
                mCityAdapter.setSelectedPosition(position);
                view.setBackgroundColor(getResources().getColor(R.color.common_bg_blue_color));
                //刷新区列表
                areaData.clear();
                GetAreaData(provience, city);
                if (onResultSelectListener != null) {
                    onResultSelectListener.onResultSelected(false, provience, city, null);
                }
                mCityAdapter.notifyDataSetInvalidated();


            }
        });
        mAreaLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                area = areaData.get(position);
                mAreaAdapter.setSelectedPosition(position);
                view.setBackgroundColor(getResources().getColor(R.color.common_bg_blue_color));
                //处理结果!
                if (onResultSelectListener != null) {
                    onResultSelectListener.onResultSelected(true, provience, city, area);
                }
                mAreaAdapter.notifyDataSetInvalidated();
            }
        });

        initProviceData();
    }

    private void initProviceData() {

        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    provienceData.add(object.getString("name"));
                }
                mProvienceAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void GetCityData(String provience) {
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject provienceObject = jsonArray.getJSONObject(i);

                    if (provienceObject.getString("name").equals(provience)) {
                        //获取对应省信息
                        JSONArray cityListJson = provienceObject.getJSONArray("cityList");
                        for (int j = 0; j < cityListJson.length(); j++) {
                            JSONObject cityObject = cityListJson.getJSONObject(j);
                            cityData.add(cityObject.getString("name"));
                        }
                        break;
                    }

                }
                mCityAdapter.notifyDataSetChanged();
                mCityLv.setSelection(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void GetAreaData(String provience, String city) {
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject provienceObject = jsonArray.getJSONObject(i);

                    if (provienceObject.getString("name").equals(provience)) {
                        //获取对应省信息
                        JSONArray cityListJson = provienceObject.getJSONArray("cityList");
                        for (int j = 0; j < cityListJson.length(); j++) {
                            JSONObject cityObject = cityListJson.getJSONObject(j);
                            if (cityObject.getString("name").equals(city)) {
                                //获取对应市信息
                                JSONArray areaJson = cityObject.getJSONArray("areaList");
                                for (int n = 0; n < areaJson.length(); n++) {
                                    String area = areaJson.get(n).toString();
                                    areaData.add(area);
                                }
                                break;
                            }
                        }
                    }
                }
                mAreaAdapter.notifyDataSetChanged();
                mAreaLv.setSelection(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public interface OnResultSelectListener {

        public void onResultSelected(boolean isFinish, String provience, String city, String district);

    }

    public class CommonAdapter extends BaseAdapter {
        private List<String> ListData;
        private Context context;
        private int selectedPosition = -1;

        public void setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
        }


        public CommonAdapter(Context context, List<String> ListData) {
            this.context = context;
            this.ListData = ListData;
        }

        @Override
        public int getCount() {
            return ListData.size();
        }

        @Override
        public Object getItem(int position) {
            return ListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Viewholder holder;
            if (convertView == null) {
                holder = new Viewholder();
                convertView = View.inflate(context, R.layout.item_address, null);

                holder.mTextView = (TextView) convertView.findViewById(R.id.item_name);

                convertView.setTag(holder);
            } else {
                holder = (Viewholder) convertView.getTag();
            }

            Log.v("测试", "selectedPosition:" + selectedPosition + " position=" + position);
            holder.mTextView.setText(ListData.get(position) + "");
            if (selectedPosition == position) {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.common_bg_blue_color));
            } else {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }


            return convertView;
        }

        public class Viewholder {
            public TextView mTextView;
        }

    }


}
