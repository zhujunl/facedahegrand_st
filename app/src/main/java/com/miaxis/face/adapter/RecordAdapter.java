package com.miaxis.face.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.bean.Record;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.util.DateUtil;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xu.nan on 2017/5/26.
 */

public class RecordAdapter extends BaseAdapter {

    private List<IDCardRecord> recordList;
    private Context context;

    public RecordAdapter(Context context) {
        this.context = context;
    }

    public void setRecordList(List<IDCardRecord> recordList) {
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        if (recordList == null) {
            return 0;
        }
        return recordList.size();
    }

    @Override
    public IDCardRecord getItem(int i) {
        if (recordList == null) {
            return null;
        }
        return recordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(Constants.VERSION ?R.layout.item_record:R.layout.item_record_860s, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        IDCardRecord record = recordList.get(i);
        holder.tvName.setText(record.getName());
        holder.tvCardNo.setText(record.getCardNumber());
        holder.tvOrg.setText(record.getOrgName());
        holder.tvResult.setText(record.getDescribe());
        if (record.isVerifyResult()) {
            holder.tvResult.setTextColor(context.getResources().getColor(R.color.green_dark));
        } else {
            holder.tvResult.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.tvOpdate.setText(DateUtil.toAll(record.getVerifyTime()));

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_cardNo)
        TextView tvCardNo;
        @BindView(R.id.tv_result)
        TextView tvResult;
        @BindView(R.id.tv_org)
        TextView tvOrg;
        @BindView(R.id.tv_opdate)
        TextView tvOpdate;
        @BindView(R.id.ll_item)
        LinearLayout llItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
