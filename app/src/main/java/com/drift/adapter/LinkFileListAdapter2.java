package com.drift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drift.app.ForeamApp;
import com.drift.camcontroldemo.R;
import com.drift.foreamlib.boss.model.HTMLFile;
import com.drift.foreamlib.util.StringUtil;
import com.foreamlib.imageloader.ImageLoader;

import java.util.List;


public class LinkFileListAdapter2 extends BaseAdapter {
    private static final String TAG = "LinkFileListAdapter2";
    private LayoutInflater inflater;
    private List m_list;
    private Context m_mContext;
    private ImageLoader imageloader;

    private OnDownloadClickListener m_onDownloadClickListener = null;
    private OnDeleteClickListener m_onDeleteClickListener = null;
    public LinkFileListAdapter2() {}

    public LinkFileListAdapter2(Context context, List<HTMLFile> fileInfoList) {
        m_mContext = context;
        m_list = fileInfoList;
        inflater = LayoutInflater.from(context);
        imageloader = ForeamApp.getInstance().getImageLoader();
    }

    public void setOnDownloadClickListener(OnDownloadClickListener listener) {
        m_onDownloadClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        m_onDeleteClickListener = listener;
    }

    public interface OnDownloadClickListener {
        void OnDownloadClickListener(View view, int position);
    }

    public interface OnDeleteClickListener {
        void OnDeleteClickListener(View view, int position);
    }

    @Override
    public int getCount() {
        return m_list.size();
    }

    @Override
    public Object getItem(int i) {
        return m_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        RelativeLayout llContainer;
        ImageView ivIamge;
        TextView tvDuration;
        RelativeLayout rlCheckedContainer;
        ImageView ivCheckedItem;
        TextView tvTitle;
        TextView tvDate;
        TextView tvSize;
        ImageView ivItemDownload;
        ImageView ivItemDelete;

        View view = inflater.inflate(R.layout.link_cam_file, null);
        HTMLFile data = (HTMLFile)m_list.get(i);
        llContainer = (RelativeLayout) view.findViewById(R.id.ll_container);
        ivIamge = (ImageView) view.findViewById(R.id.iv_iamge);
        tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        rlCheckedContainer = (RelativeLayout) view.findViewById(R.id.rl_checked_container);
        ivCheckedItem = (ImageView) view.findViewById(R.id.iv_checked_item);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        tvSize = (TextView) view.findViewById(R.id.tv_size);
        ivItemDownload = (ImageView) view.findViewById(R.id.iv_item_download);
        ivItemDelete = (ImageView) view.findViewById(R.id.iv_item_delete);

        String thm_url = null;
        boolean isFromMeta = false;
        switch (data.getType()) {
            case HTMLFile.TYPE_VIDEO:
//				viewHolder.ll_meta_data.setVisibility(View.VISIBLE);
//				viewHolder.iv_type_video.setVisibility(View.VISIBLE);
//				viewHolder.iv_type_video.setImageResource(R.drawable.p_file_type_video);
//                tv_size.setText(StringUtil.getShortSizeString(data.getSize()));
                if (!StringUtil.isNon(data.getThmUrl())) {
                    thm_url = data.getThmUrl();
                    isFromMeta = false;
                } else if (!StringUtil.isNon(data.getSamllFileUrl())) {
                    thm_url = data.getSamllFileUrl();
                    isFromMeta = true;
                } else {
                    thm_url = data.getBigFileUrl();
                    isFromMeta = true;
                }
//                tv_title.setText(data.getName());
//                tv_duration.setText("00:00:00");
//                tv_date.setText(data.getCreateTimeStr());
                break;
            case HTMLFile.TYPE_PHOTO:
                thm_url = data.getBigFileUrl();
                isFromMeta = true;
                break;
        }

        tvSize.setText(StringUtil.getShortSizeString(data.getSize()));
//        if (!StringUtil.isNon(data.getThmUrl())) {
//            thm_url = data.getThmUrl();
//            isFromMeta = false;
//        } else if (!StringUtil.isNon(data.getSamllFileUrl())) {
//            thm_url = data.getSamllFileUrl();
//            isFromMeta = true;
//        } else {
//            thm_url = data.getBigFileUrl();
//            isFromMeta = true;
//        }

        imageloader.bind(this, ivIamge, thm_url, R.drawable.link_default_thumb, -1, -1, thm_url + "_" + isFromMeta + "." + data.getSize()+data.getCreateTime(),
                isFromMeta, true);

        tvTitle.setText(data.getName());
        tvDuration.setText("00:00:00");
        tvDate.setText(data.getCreateTimeStr());

        if(m_onDownloadClickListener!=null)
        {
            ivItemDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_onDownloadClickListener.OnDownloadClickListener(view, i);
                }
            });

        }

        if(m_onDeleteClickListener!=null)
        {
            ivItemDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_onDeleteClickListener.OnDeleteClickListener(view,i);
                }
            });

        }

        return view;
    }

}