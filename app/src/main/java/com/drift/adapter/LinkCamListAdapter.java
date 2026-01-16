package com.drift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drift.camcontroldemo.R;
import com.drift.foreamlib.boss.model.CamStatus;

import java.util.List;

public class LinkCamListAdapter extends RecyclerView.Adapter<LinkCamListAdapter.ViewHolder> {
    private static final String TAG = "LinkCamListAdapter";
    private List m_list;
    private Context m_mContext;

    private OnRecordClickListener m_onRecordClickListener = null;
    private OnSettingClickListener m_onSettingClickListener = null;

    public LinkCamListAdapter(Context context, List<CamStatus> camInfoList) {
        m_mContext = context;
        m_list = camInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(m_mContext).inflate(R.layout.layout_link_camera_model, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CamStatus cameraStatusNew = (CamStatus)m_list.get(position);
        if(cameraStatusNew.isOffline() || !cameraStatusNew.isInit())
        {
            if(cameraStatusNew.isOffline())
            {
                holder.ibTakephoto.setImageResource(R.drawable.link_cam_offline);
                holder.rlMask.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.ibTakephoto.setImageResource(R.drawable.link_cam_online);
                holder.rlMask.setVisibility(View.INVISIBLE);
            }

            holder.tvBatVal.setText("-");
            holder.tvMemVal.setText("-");
            holder.tvResVal.setText("-");
            if(cameraStatusNew.getCamIP() == null) {
                holder.tvIpVal.setText("-");
            }
            else {
                holder.tvIpVal.setText(cameraStatusNew.getCamIP());
            }
        }
        else
        {

            //only test code
            if(cameraStatusNew.getmStreamSetting()==null)
            {
                holder.rlMask.setVisibility(View.VISIBLE);
                holder.tvIpVal.setText(cameraStatusNew.getCamIP());
                return;
            }
            //only test code
            holder.rlMask.setVisibility(View.INVISIBLE);
            holder.tvBatVal.setText(cameraStatusNew.getmCameraStatus().getBattery()+"%");
            if(cameraStatusNew.getmCameraStatus().getSd_total()!=0) {
                long memRate = (long)cameraStatusNew.getmCameraStatus().getSd_free() * 100 / cameraStatusNew.getmCameraStatus().getSd_total();
//                memRate = new BigDecimal(memRate).setScale(0, BigDecimal.ROUND_HALF_UP)
//                        .floatValue();
                long lMemRate = (long)memRate+1;
                //如果超出了100,就还是保持为100
                if(lMemRate>100)
                    lMemRate = 100;
                holder.tvMemVal.setText(lMemRate + "%");
            }
            else
                holder.tvMemVal.setText("0%");
//            holder.tvResVal.setText("-");
//            String resAndFpsInfo = converRes(cameraStatusNew.getmVideoSetting().getVideo_res())+"@"+convertFps(cameraStatusNew.getmVideoSetting().getVideo_framerate());
            String resAndFpsInfo = converRes(cameraStatusNew.getmVideoSetting().getVideo_res(), cameraStatusNew.getmCameraStatus().getModel_name())+"@"+cameraStatusNew.getmVideoSetting().getVideo_framerate();
            holder.tvResVal.setText(resAndFpsInfo);
            holder.tvIpVal.setText(cameraStatusNew.getCamIP());
//            if(cameraStatusNew.getmCameraSettingNew().getHd_record()==1) {
                holder.ibTakephoto.setVisibility(View.VISIBLE);
//                if (cameraStatusNew.getmCameraStatus().getCapture_mode() == 0) {
//                    if (cameraStatusNew.getmCameraStatus().getRec_time() == 0) {
//                        holder.ibTakephoto.setImageResource(R.drawable.link_startrecord_btn);
//                    } else {
//                        holder.ibTakephoto.setImageResource(R.drawable.link_stoprecord_btn);
//                    }
//                } else {
//                    holder.ibTakephoto.setImageResource(R.drawable.link_takephoto_btn);
//                }
//            }
//            else
//            {
//                holder.ibTakephoto.setVisibility(View.INVISIBLE);
//            }
            holder.ibTakephoto.setImageResource(R.drawable.link_cam_online);
        }

        if(m_onRecordClickListener!=null)
        {
            holder.ibTakephoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_onRecordClickListener.OnRecordClickListener(view, holder.getAdapterPosition());
                }
            });

        }

        if(m_onSettingClickListener!=null)
        {
            holder.rlModel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_onSettingClickListener.OnSettingClickListener(view, holder.getAdapterPosition());
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return m_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlModel;
        private RelativeLayout rlBat;
        private TextView tvBat;
        private TextView tvBatVal;
        private RelativeLayout rlMemory;
        private TextView tvMemory;
        private TextView tvMemVal;
        private RelativeLayout rlRes;
        private TextView tvRes;
        private TextView tvResVal;
        private RelativeLayout rlIp;
        private TextView tvIp;
        private TextView tvIpVal;
        private ImageButton ibTakephoto;
        private RelativeLayout rlMask;

        public ViewHolder(View view) {
            super(view);
            rlModel = (RelativeLayout) view.findViewById(R.id.rl_model);
            rlBat = (RelativeLayout) view.findViewById(R.id.rl_bat);
            tvBat = (TextView) view.findViewById(R.id.tv_bat);
            tvBatVal = (TextView) view.findViewById(R.id.tv_bat_val);
            rlMemory = (RelativeLayout) view.findViewById(R.id.rl_memory);
            tvMemory = (TextView) view.findViewById(R.id.tv_memory);
            tvMemVal = (TextView) view.findViewById(R.id.tv_mem_val);
            rlRes = (RelativeLayout) view.findViewById(R.id.rl_res);
            tvRes = (TextView) view.findViewById(R.id.tv_res);
            tvResVal = (TextView) view.findViewById(R.id.tv_res_val);
            rlIp = (RelativeLayout) view.findViewById(R.id.rl_ip);
            tvIp = (TextView) view.findViewById(R.id.tv_ip);
            tvIpVal = (TextView) view.findViewById(R.id.tv_ip_val);
            ibTakephoto = (ImageButton) view.findViewById(R.id.ib_takephoto);
            rlMask = (RelativeLayout) view.findViewById(R.id.rl_mask);
        }
    }
    public void setOnRecordClickListener(OnRecordClickListener listener) {
        m_onRecordClickListener = listener;
    }

    public void setOnSettingClickListener(OnSettingClickListener listener) {
        m_onSettingClickListener = listener;
    }

    public interface OnRecordClickListener {
        void OnRecordClickListener(View view, int position);
    }

    public interface OnSettingClickListener {
        void OnSettingClickListener(View view, int pos);
    }

    private String converRes(int value, String modelName)
    {
        /*
                VRES_4K = 0,
                VRES_4KUHD,  //1
                VRES_27K,    //2
                VRES_1080P,   //3
                VRES_720P,    //4
                VRES_WVGA,    //5
            */
        String resStr = "";
        //Ghost X,Ghost XL,X3
        if(modelName.equals("Ghost X") || modelName.equals("Ghost XL") || modelName.equals("X3"))
        {//Ghost X,Ghost XL,X3: 0 (1080P), 2(720P), 3(WVGA)
            switch (value)
            {
                case 0:
                    resStr = "1080P";
                    break;
                case 2:
                    resStr = "720P";
                    break;
                case 3:
                    resStr = "WVGA";
                    break;
                default:
                    resStr = "--";
                    break;
            }
        }
        else
        {//Ghost 4K+, N1, N2, XL Pro 0(4K), 1(4KUHD), 2(27K), 3(1080P), 4(720P),5(WVGA).
            switch (value)
            {
                case 0:
                    resStr = "4K";
                    break;
                case 1:
                    resStr = "4KUHD";
                    break;
                case 2:
                    resStr = "2.7K";
                    break;
                case 3:
                    resStr = "1080P";
                    break;
                case 4:
                    resStr = "720P";
                    break;
                case 5:
                    resStr = "WVGA";
                    break;
                default:
                    resStr = "--";
                    break;
            }
        }
        return resStr;
    }

    private String convertFps(int value) {
         /*
            VFR_24,
             VFR_25,
             VFR_30,
             VFR_48,
             VFR_50,
             VFR_60,
             VFR_100,
             VFR_120,
             VFR_200,
             VFR_240,
            */
        String fpsStr = "";
        switch (value)
        {
            case 0:
                fpsStr = "24";
                break;
            case 1:
                fpsStr = "25";
                break;
            case 2:
                fpsStr = "30";
                break;
            case 3:
                fpsStr = "48";
                break;
            case 4:
                fpsStr = "50";
                break;
            case 5:
                fpsStr = "60";
                break;
            case 6:
                fpsStr = "100";
                break;
            case 7:
                fpsStr = "120";
                break;
            case 8:
                fpsStr = "200";
                break;
            case 9:
                fpsStr = "240";
                break;
            default:
                fpsStr = "--";
                break;
        }
        return fpsStr;

    }
}