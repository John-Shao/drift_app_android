package com.drift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drift.camcontroldemo.R;
import com.drift.foreamlib.boss.model.CamStatus;

import java.util.List;

import com.drift.foreamlib.boss.model.HTMLFile;
import com.drift.foreamlib.util.StringUtil;
import com.foreamlib.imageloader.ImageLoader;
import com.drift.app.ForeamApp;


public class LinkFileListAdapter extends RecyclerView.Adapter<LinkFileListAdapter.ViewHolder> {
    private static final String TAG = "LinkFileListAdapter";
    private List m_list;
    private Context m_mContext;
    private ImageLoader imageloader;

    private OnDownloadClickListener m_onDownloadClickListener = null;
    private OnDeleteClickListener m_onDeleteClickListener = null;

    public LinkFileListAdapter(Context context, List<HTMLFile> fileInfoList) {
        m_mContext = context;
        m_list = fileInfoList;
        imageloader = ForeamApp.getInstance().getImageLoader();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(m_mContext).inflate(R.layout.link_cam_file, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        HTMLFile data = (HTMLFile)m_list.get(position);

        holder.tvSize.setText(StringUtil.getShortSizeString(data.getSize()));
        String thm_url = null;
        boolean isFromMeta = false;
        if (!StringUtil.isNon(data.getThmUrl())) {
            thm_url = data.getThmUrl();
        } else if (!StringUtil.isNon(data.getSamllFileUrl())) {
            thm_url = data.getSamllFileUrl();
        } else {
            thm_url = data.getBigFileUrl();
        }
//        imageloader.bind((BaseAdapter) this, holder.ivIamge, data.getThmUrl(), R.drawable.link_default_thumb, -1, -1, thm_url + "_" + isFromMeta + "." + data.getSize()+data.getCreateTime(),
//                isFromMeta, true);

        holder.tvTitle.setText(data.getName());
        holder.tvDuration.setText("00:00:00");
        holder.tvDate.setText(data.getCreateTimeStr());

        if(m_onDownloadClickListener!=null)
        {
            holder.ivItemDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_onDownloadClickListener.OnDownloadClickListener(view, holder.getAdapterPosition());
                }
            });

        }

        if(m_onDeleteClickListener!=null)
        {
            holder.ivItemDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_onDeleteClickListener.OnDeleteClickListener(view, holder.getAdapterPosition());
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return m_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout llContainer;
        private ImageView ivIamge;
        private TextView tvDuration;
        private RelativeLayout rlCheckedContainer;
        private ImageView ivCheckedItem;
        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvSize;
        private ImageView ivItemDownload;
        private ImageView ivItemDelete;

        public ViewHolder(View view) {
            super(view);
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
        }
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


}