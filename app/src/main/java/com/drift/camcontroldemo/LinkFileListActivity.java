package com.drift.camcontroldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drift.adapter.LinkCamListAdapter;
import com.drift.adapter.LinkCamListItemDecoration;
import com.drift.adapter.LinkFileListAdapter;
import com.drift.foreamlib.boss.model.CamStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.drift.foreamlib.boss.model.HTMLFile;
import com.drift.app.ForeamApp;
import com.drift.foreamlib.local.ctrl.LocalListener;
import com.drift.foreamlib.local.ctrl.LocalController;
import com.drift.foreamlib.boss.model.HTMLFile;


public class LinkFileListActivity extends AppCompatActivity {

    private static String TAG = "LinkFileListActivity";
    private RelativeLayout rlNav;
    private RelativeLayout rlBack;
    private ImageView ivBack;
    private RecyclerView rvList;
    private Map monMap = new HashMap();
    private int folderFetchingIndex = 0;
    List<String> mFolderList = new ArrayList<String>();
    List<HTMLFile> mList = new ArrayList<HTMLFile>();
    private LinkFileListAdapter m_fileListRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_file_list);
        rlNav = (RelativeLayout) findViewById(R.id.rl_nav);
        rlBack = (RelativeLayout) findViewById(R.id.rl_back);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivBack = (ImageView) findViewById(R.id.iv_back);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        //初始化月份的hash数组
        monMap.put("Jan", "01");
        monMap.put("Feb", "02");
        monMap.put("Mar", "03");
        monMap.put("Apr", "04");
        monMap.put("May", "05");
        monMap.put("Jun", "06");
        monMap.put("Jul", "07");
        monMap.put("Aug", "08");
        monMap.put("Sep", "09");
        monMap.put("Oct", "10");
        monMap.put("Nov", "11");
        monMap.put("Dec", "12");

        initRecycleViewAdapter();

        //获取相机文件夹内容
        getCamFolder(ForeamApp.getInstance().getCurrentCamIP());
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForeamApp.getInstance().stopInternetTask();//
    }

    private void getCamFolder(String ipAddr)
    {
        new LocalController().getCamFolders(ipAddr, new LocalListener.OnGetCamFoldersListener() {
            @Override
            public void onGetCamFolders(boolean success, String foldersData, int amount) {
                if(success) {
                    foldersData = foldersData.substring(0, foldersData.length()-1);
                    foldersData = "["+foldersData+"]";
                    try{
                        JSONArray jsonArray = new JSONArray(foldersData);
                        folderFetchingIndex = 0;
                        mFolderList.clear();
                        for(int i = 0;i < jsonArray.length();i++){
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            mFolderList.add(jsonObject.getString("Path"));
                        }
                        if(mFolderList.size()>0)
                        {//获取文件夹内容
//                            get4KCamFile();
                            //更改为http命令获取文件夹内容
                            mList.clear();
                            //先获取第一个文件夹的内容
                            String parentFolderName = mFolderList.get((mFolderList.size() - 1) - folderFetchingIndex);
                            getCamFile(ForeamApp.getInstance().getCurrentCamIP(), parentFolderName);
                        }
                        else
                        {
//                            onFetchRealData(ErrorCode.SUCCESS, mList, 1, mList.size());
                            m_fileListRecycleAdapter.notifyDataSetChanged();
                        }
                    }catch (Exception e){e.printStackTrace();}

                }else
                {//没有文件夹,没有文件
                    mFolderList.clear();
                    mList.clear();
//                    onFetchRealData(ErrorCode.SUCCESS, mList, 1, mList.size());
                    m_fileListRecycleAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    public void getFirstCamFile() {
        Log.e(TAG, "Come to get4KCamFile");
        //更改为http命令获取文件夹内容
        mList.clear();
        //先获取第一个文件夹的内容
        String parentFolderName = mFolderList.get((mFolderList.size() - 1) - folderFetchingIndex);
        getCamFile(ForeamApp.getInstance().getCurrentCamIP(), parentFolderName);
    }

    private void getCamFile(String ipAddr, String folderName)
    {
        new LocalController().getCamFiles(ipAddr, folderName, new LocalListener.OnGetCamFilesListener() {
            @Override
            public void onGetCamFiles(boolean success, String filesData, int amount) {
                if(success) {
                    //要考虑空文件夹的情况,这个根据返回值进行?
                    filesData = filesData.substring(0, filesData.length()-1);
                    filesData = "["+filesData+"]";
                    try{
                        JSONArray jsonArray = new JSONArray(filesData);
                        for(int i = 0;i < jsonArray.length();i++){
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            HTMLFile file = new HTMLFile();
                            String parentFolderName = mFolderList.get((mFolderList.size()-1)-folderFetchingIndex);
                            String baseUrl = "http://"+ForeamApp.getInstance().getCurrentCamIP()+"/DCIM/"+parentFolderName;
                            String fileName = jsonObject.getString("Path");
                            String fileSize = jsonObject.getString("Size");
                            String createTime = jsonObject.getString("CreateTime");
                            //"Apr 02 17:26:52 2021" --amba标准版返回格式是"2021-04-10 01:39:50"
                            String createdMonE = createTime.substring(0, 3);
                            String createdM = monMap.get(createdMonE).toString();
                            String createdD = createTime.substring(4, 6);
                            String createdT = createTime.substring(7, 15);
                            String createdY = createTime.substring(createTime.length()-4, createTime.length());

                            String createTimeConvert = createdY+"-"+createdM+"-"+createdD+" "+createdT;
                            Long lCreateTime = getTime(createTimeConvert);
                            //VID00011.THM
                            String thmFlag = jsonObject.getString("Thumb");
                            String thmUrl = null;
                            if(thmFlag.equals("1"))
                            {
                                thmUrl = baseUrl+"/"+fileName.substring(0,fileName.length()-4)+".THM";
                                file.setThmUrl(thmUrl);
                            }
                            String bigFileUrl = baseUrl+"/"+fileName;
                            file.setBigFileUrl(bigFileUrl);
                            file.setSize(Long.parseLong(fileSize));
                            //暂时只支持视频文件
                            file.setType(HTMLFile.TYPE_VIDEO);
                            file.setParentFolderName(parentFolderName);
                            String name = fileName.substring(0, 8);
                            String extendName = fileName.substring(fileName.length() - 3, fileName.length());
                            file.setName(name);
                            file.setExtendName(extendName);
                            file.setCreateTimeStr(createTimeConvert);
                            file.setCreateTime(lCreateTime);
                            //不断往第一个插入,因为文件夹和文件都是按时间顺序排列
                            mList.add(0,file);
                        }
                        if(folderFetchingIndex<mFolderList.size()-1)
                        {//继续获取文件夹内容
                            folderFetchingIndex++;
                            String parentFolderName = mFolderList.get((mFolderList.size()-1)-folderFetchingIndex);
                            getCamFile(ForeamApp.getInstance().getCurrentCamIP(), parentFolderName);
                        }
                        else
                        {
//                            onFetchRealData(ErrorCode.SUCCESS, mList, 1, mList.size());
                            m_fileListRecycleAdapter.notifyDataSetChanged();
                        }
                    }catch (Exception e){e.printStackTrace();}

                }
                else
                {//文件夹没有文件,继续下一个文件夹,不用修改list的文件
                    if(folderFetchingIndex<mFolderList.size()-1)
                    {//继续获取文件夹内容
                        folderFetchingIndex++;
                        String parentFolderName = mFolderList.get((mFolderList.size()-1)-folderFetchingIndex);
                        getCamFile(ForeamApp.getInstance().getCurrentCamIP(), parentFolderName);
                    }
                    else
                    {
//                        onFetchRealData(ErrorCode.SUCCESS, mList, 1, mList.size());
                        m_fileListRecycleAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void initRecycleViewAdapter()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(LinkFileListActivity.this);
        rvList.setLayoutManager(layoutManager);
//        rvList.addItemDecoration(new LinkCamListItemDecoration(dip2px(15),dip2px(15)));
        m_fileListRecycleAdapter = new LinkFileListAdapter(LinkFileListActivity.this, mList);
        m_fileListRecycleAdapter.setOnDownloadClickListener(new LinkFileListAdapter.OnDownloadClickListener() {
            @Override
            public void OnDownloadClickListener(View view, int position) {
                //采用http进行下载文件
                final HTMLFile file = mList.get(position);
                Toast.makeText(LinkFileListActivity.this, "下载地址是:"+file.getBigFileUrl(), Toast.LENGTH_SHORT).show( );
            }
        });
        m_fileListRecycleAdapter.setOnDeleteClickListener(new LinkFileListAdapter.OnDeleteClickListener() {
            @Override
            public void OnDeleteClickListener(View view, int position) {
                final HTMLFile file = mList.get(position);
                String deletePath = file.getParentFolderName()+"/"+file.getName();
                new LocalController().delFile(ForeamApp.getInstance().getCurrentCamIP(), ""+deletePath, new LocalListener.OnCommonResListener() {
                    @Override
                    public void onCommonRes(boolean success) {
                        mList.remove(position);
                        m_fileListRecycleAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        rvList.setAdapter(m_fileListRecycleAdapter);
    }

    private Long getTime(String timedata) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = format.parse(timedata);

            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();

        }

        return null;
    }


}