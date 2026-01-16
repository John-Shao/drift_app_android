package com.drift.foreamlib.api;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.collection.ArrayMap;

import com.drift.app.ForeamApp;
import com.drift.define.Intents;
import com.drift.foreamlib.boss.model.HTMLFile;
import com.drift.foreamlib.local.ctrl.LocalController;
import com.drift.foreamlib.local.ctrl.LocalListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForeamCamFileCtrl {
    private final static String TAG = "ForeamCamFileCtrl";
    private OnResponeListener mOnResponeListener;
    private List<String> mFolderList = new ArrayList<String>();
    private List<HTMLFile> mList = new ArrayList<HTMLFile>();
    private boolean mIsOperating = false;
    private int mOperationType;
    private String mCamIP;
    private String mFileName;
    private boolean mIncludingDatFile;

    private int folderFetchingIndex = 0;
    private int keyInfoFetchingIndex = 0;
    private int mSearchFileIndex = -1;
    private Map monMap = new HashMap();


    public interface OnResponeListener {
        public void onResponeCallback(int status, List<HTMLFile> list, int index, int operationType);
    }

    private ForeamCamFileCtrl() {
//        initMonMap();
    }

    public static synchronized ForeamCamFileCtrl getInstance() {
        return ForeamCamCtrlHolder.instance;
    }

    private static class ForeamCamCtrlHolder {
        private static final ForeamCamFileCtrl instance = new ForeamCamFileCtrl();
    }

    public void deleteFile(String camIp, String fileName) {
        setIsOperating(true);
        mOperationType = Intents.DELETE_FILE;
        mSearchFileIndex = -1;
        mFileName = fileName;
        mCamIP = camIp;
//        getCamFolderWithDat(mCamIP);
        getMediaFile(mCamIP);
    }

    public void downloadFile(String camIp, String fileName) {
        setIsOperating(true);
        mOperationType = Intents.DOWLOAD_FILE;
        mSearchFileIndex = -1;
        mFileName = fileName;
        mCamIP = camIp;
//        getCamFolderWithDat(mCamIP);
        getMediaFile(mCamIP);
    }

    public void searchFile(String camIp, String fileName) {
        setIsOperating(true);
        mOperationType = Intents.SEARCH_FILE;
        mSearchFileIndex = -1;
        mFileName = fileName;
        mCamIP = camIp;
//        getCamFolderWithDat(mCamIP);
        getMediaFile(mCamIP);
    }

    public void startRecord(String camIp, String fileName) {
        setIsOperating(true);
        mOperationType = Intents.CREATE_RECORD_FILE;
        mSearchFileIndex = -1;
        mFileName = fileName;
        mCamIP = camIp;
        getMediaFile(mCamIP);
    }

    public void takePhoto(String camIp, String fileName) {
        setIsOperating(true);
        mOperationType = Intents.CREATE_PHOTO_FILE;
        mSearchFileIndex = -1;
        mFileName = fileName;
        mCamIP = camIp;
        getMediaFile(mCamIP);
    }

    public void fetchFiles(String camIp) {
        setIsOperating(true);
        mOperationType = Intents.FETCH_FILE_LIST;
        mSearchFileIndex = -1;
        mCamIP = camIp;
//        getCamFolderWithDat(mCamIP);
        getMediaFile(mCamIP);
    }

    public boolean isOperating() {
        return mIsOperating;
    }

    public void setIsOperating(boolean mIsOperating) {
        this.mIsOperating = mIsOperating;
    }


//    private void getCamFolderWithDat(String ipAddr) {
////        isFilefirst = false;
//        Log.d(TAG, "drift test:come to getCamFolderWithDat");
//        new LocalController().getCamFolders(ipAddr, new LocalListener.OnGetCamFoldersListener() {
//            @Override
//            public void onGetCamFolders(boolean success, String foldersData, int amount) {
//                Log.d(TAG, "drift test:success is " + success + " foldersData is " + foldersData);
//                if (success) {
//                    foldersData = foldersData.substring(0, foldersData.length() - 1);
//                    foldersData = "[" + foldersData + "]";
//                    try {
//                        JSONArray jsonArray = new JSONArray(foldersData);
//                        folderFetchingIndex = 0;
//                        mFolderList.clear();
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                            mFolderList.add(0, jsonObject.getString("Path"));
//                        }
//                        if (mFolderList.size() > 0) {//获取文件夹内容
//                            get4KCamFileWithDat();
//                        } else {
//                            setIsOperating(false);
//                            if (mOnResponeListener != null) {
//                                mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        setIsOperating(false);
//                        if (mOnResponeListener != null) {
//                            mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
//                        }
//                    }
//
//                } else {//没有文件夹,没有文件
//                    mFolderList.clear();
//                    mList.clear();
//                    setIsOperating(false);
//                    if (mOnResponeListener != null) {
//                        mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
//                    }
//                }
//            }
//        });
//    }

//    public void get4KCamFileWithDat() {
//        Log.e(TAG, "Come to get4KCamFile");
//        //更改为http命令获取文件夹内容
//        mList.clear();
//        //先获取第一个文件夹的内容
//        String parentFolderName = mFolderList.get((mFolderList.size() - 1) - folderFetchingIndex);
//        getCamFileWithDat(mCamIP, parentFolderName);
//    }

//    private void getCamFileWithDat(String ipAddr, String folderName) {
//        Log.d(TAG, "drift test:come to getCamFileWithDat");
//        new LocalController().getCamFiles(ipAddr, folderName, new LocalListener.OnGetCamFilesListener() {
//            @Override
//            public void onGetCamFiles(boolean success, String filesData, int amount) {
//                Log.d(TAG, "drift test:success is " + success + " filesData is " + filesData);
//                if (success) {
//                    //要考虑空文件夹的情况,这个根据返回值进行?
//                    filesData = filesData.substring(0, filesData.length() - 1);
//                    filesData = "[" + filesData + "]";
//                    try {
//                        JSONArray jsonArray = new JSONArray(filesData);
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                            HTMLFile file = new HTMLFile();
//                            String parentFolderName = mFolderList.get((mFolderList.size() - 1) - folderFetchingIndex);
//                            String baseUrl = "http://" + ForeamApp.getInstance().getCurrentCamIP() + "/DCIM/" + parentFolderName;
//                            String fileName = jsonObject.getString("Path");
//                            String fileSize = jsonObject.getString("Size");
//                            String createTime = jsonObject.getString("CreateTime");
//                            //"Apr 02 17:26:52 2021" --amba标准版返回格式是"2021-04-10 01:39:50"
//                            String createdMonE = createTime.substring(0, 3);
//                            String createdM = monMap.get(createdMonE).toString();
//                            String createdD = createTime.substring(4, 6);
//                            String createdT = createTime.substring(7, 15);
//                            String createdY = createTime.substring(createTime.length() - 4, createTime.length());
//
//                            String createTimeConvert = createdY + "-" + createdM + "-" + createdD + " " + createdT;
//                            Long lCreateTime = getTime(createTimeConvert);
//                            //VID00011.THM
//                            String thmFlag = jsonObject.getString("Thumb");
//                            String thmUrl = null;
//                            if (thmFlag.equals("1")) {
//                                thmUrl = baseUrl + "/" + fileName.substring(0, fileName.length() - 4) + ".THM";
//                                file.setThmUrl(thmUrl);
//                            }
//                            String bigFileUrl = baseUrl + "/" + fileName;
//                            file.setBigFileUrl(bigFileUrl);
//                            file.setSize(Long.parseLong(fileSize));
//                            //暂时只支持视频文件
//                            file.setType(HTMLFile.TYPE_VIDEO);
//                            file.setParentFolderName(parentFolderName);
//                            String name = fileName.substring(0, 8);
//                            String extendName = fileName.substring(fileName.length() - 3, fileName.length());
//                            file.setName(name);
//                            file.setExtendName(extendName);
//                            file.setCreateTimeStr(createTimeConvert);
//                            file.setCreateTime(lCreateTime);
//                            //不断往第一个插入,因为文件夹和文件都是按时间顺序排列
//                            mList.add(0, file);
//                        }
//                        if (folderFetchingIndex < mFolderList.size() - 1) {//继续获取文件夹内容
//                            folderFetchingIndex++;
//                            String parentFolderName = mFolderList.get((mFolderList.size() - 1) - folderFetchingIndex);
//                            getCamFileWithDat(mCamIP, parentFolderName);
//                        } else {
//                            if (mList.size() > 0) {
////                                HTMLFile videoFile = mList.get(0);
////                                String fileUrl = "http://"+camIP+"/DCIM/"+videoFile.getParentFolderName()+"/"+videoFile.getNameWithExtend();
////                                downloadFile(fileUrl, getDownloadPath(), videoFile.getNameWithExtend());
//                                getFilesKeyInfo();
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        setIsOperating(false);
//                        if (mOnResponeListener != null) {
//                            mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
//                        }
//                    }
//
//                } else {//文件夹没有文件,继续下一个文件夹,不用修改list的文件
//                    if (folderFetchingIndex < mFolderList.size() - 1) {//继续获取文件夹内容
//                        folderFetchingIndex++;
//                        String parentFolderName = mFolderList.get((mFolderList.size() - 1) - folderFetchingIndex);
//                        getCamFileWithDat(mCamIP, parentFolderName);
//                    } else {
////                        onFetchRealData(ErrorCode.SUCCESS, mList, 1, mList.size());
//                        if (mList.size() > 0) {
////                            HTMLFile videoFile = mList.get(0);
////                            String fileUrl = "http://"+camIP+"/DCIM/"+videoFile.getParentFolderName()+"/"+videoFile.getNameWithExtend();
////                            downloadFile(fileUrl, getDownloadPath(), videoFile.getNameWithExtend());
//                            getFilesKeyInfo();
//                        }
//                    }
//                }
//            }
//        });
//    }

//    private void initMonMap() {
//        //初始化月份的hash数组
//        monMap.put("Jan", "01");
//        monMap.put("Feb", "02");
//        monMap.put("Mar", "03");
//        monMap.put("Apr", "04");
//        monMap.put("May", "05");
//        monMap.put("Jun", "06");
//        monMap.put("Jul", "07");
//        monMap.put("Aug", "08");
//        monMap.put("Sep", "09");
//        monMap.put("Oct", "10");
//        monMap.put("Nov", "11");
//        monMap.put("Dec", "12");
//    }

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

    public void setOnResponeListener(OnResponeListener OnResponeListener) {
        this.mOnResponeListener = OnResponeListener;
    }

//    private void getFilesKeyInfo() {//获取文件的key值
//        keyInfoFetchingIndex = 0;
//        if (keyInfoFetchingIndex < mList.size()) {
//            HTMLFile videoFile = mList.get(keyInfoFetchingIndex);
//            String fileUrl = "http://" + mCamIP + "/DCIM/" + videoFile.getParentFolderName() + "/" + videoFile.getNameWithExtend();
//            fileUrl = fileUrl.replace("mp4", "dat");
//            fileUrl = fileUrl.replace("MP4", "dat");
//            getDatFile(fileUrl);
//        } else {
//            setIsOperating(false);
//            if (mOnResponeListener != null) {
//                mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
//            }
//        }
//
//    }

    private void getMediaFile(String ipAddr) {
        //清空数据
        mList.clear();
        String fileUrl = "http://" + mCamIP + "/MISC/" + "mediaInfo";
        Log.d(TAG, "drift test: come to getDatFile, fileUrl is " + fileUrl);
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(fileUrl)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                setIsOperating(false);
                if (mOnResponeListener != null) {
                    mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e(TAG, "onResponse: " + response.body().string());
                Log.d(TAG, "drift test: success to getMediaFile");
                String filesData = response.body().string();
                filesData.split("/");
                try
                {
                    JSONObject filesObject = new JSONObject(filesData);
                    JSONArray jsonArray = filesObject.getJSONArray("files");//new JSONArray(filesData);
                    Log.d(TAG, "drift test: jsonArray size is" + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        HTMLFile file = new HTMLFile();
                        //{"path":"DCIM\\100MEDIA\\VID00001.MP4","name":"car_test01","size":3946496,"time":"2022-08-01 14:01:05"}
                        String path = jsonObject.getString("path");
                        path = path.replace("\\", "/");
                        String[] splitInfo = path.split("\\/");
                        String parentFolderName = splitInfo[1];
                        String baseUrl = "http://" + ForeamApp.getInstance().getCurrentCamIP() + "/DCIM/" + parentFolderName;
                        String fileName = splitInfo[2];//jsonObject.getString("Path");
                        String fileSize = jsonObject.getString("size");
                        String createTime = jsonObject.getString("time");
                        String keyInfo = jsonObject.getString("name");
                        String bigFileUrl = baseUrl + "/" + fileName;
                        file.setBigFileUrl(bigFileUrl);
                        file.setSize(Long.parseLong(fileSize));
                        //暂时只支持视频文件
                        file.setType(HTMLFile.TYPE_VIDEO);
                        file.setParentFolderName(parentFolderName);
                        String name = fileName.substring(0, 8);
                        String extendName = fileName.substring(fileName.length() - 3, fileName.length());
                        file.setName(name);
                        file.setExtendName(extendName);
                        file.setCreateTimeStr(createTime);
                        file.setKeyString(keyInfo);
//                        file.setCreateTime(createTime);
                        //不断往第一个插入,因为文件夹和文件都是按时间顺序排列
                        mList.add(0, file);
                    }
                    setIsOperating(false);
                    if (mList.size()>0 && mOperationType != Intents.FETCH_FILE_LIST) {
                        mSearchFileIndex = getFilesWithKeyInfo(mFileName);
                    }
                    if (mOnResponeListener != null) {
                        mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setIsOperating(false);
                    if (mOnResponeListener != null) {
                        mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
                    }
                }
            }
        });
    }

//    private void getDatFile(String fileUrl) {
//        String url = "http://192.168.2.130/DCIM/100MEDIA/VID00001.dat";
//        Log.d(TAG, "drift test: come to getDatFile, fileUrl is " + fileUrl);
////        OkHttpClient okHttpClient = new OkHttpClient();
////        final Request request = new Request.Builder()
////                .url(fileUrl)
////                .get()//默认就是GET请求，可以不写
////                .build();
////        Call call = okHttpClient.newCall(request);
////        call.enqueue(new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////                Log.d(TAG, "onFailure: ");
////            }
////
////            @Override
////            public void onResponse(Call call, Response response) throws IOException {
//////                Log.e(TAG, "onResponse: " + response.body().string());
////                Log.d(TAG, "drift test: success to getDatFile");
////                char replaceChar = 0x0a;
////                String datString = response.body().string();
////                String[] strSplit = datString.split(""+replaceChar);
////                String id1 = "";
////                String id2 = "";
////                if(strSplit.length>=2) {
////                    id1 = strSplit[0].replace("pri_id=","");
////                    id2 = strSplit[1].replace("sec_id=","");
////                    Log.e(TAG, "id1 is " + id1 +"; id2 is " + id2 +"; keyis"+id1+"_"+id2);
////                    String keyStr = id1+"_"+id2;
////                    HTMLFile videoFile = mList.get(keyInfoFetchingIndex);
////                    videoFile.setKeyString(keyStr);
////
////                    if(keyInfoFetchingIndex<mList.size()-1) {
////                        keyInfoFetchingIndex++;
////                        videoFile = mList.get(keyInfoFetchingIndex);
////                        String fileUrl = "http://" + mCamIP + "/DCIM/" + videoFile.getParentFolderName() + "/" + videoFile.getNameWithExtend();
////                        fileUrl = fileUrl.replace("mp4", "dat");
////                        fileUrl = fileUrl.replace("MP4", "dat");
////                        getDatFile(fileUrl);
////                    }
////                    else
////                    {
//////                        Looper.prepare();
//////                        Toast.makeText(getBaseContext(), "同步文件列表成功", Toast.LENGTH_SHORT).show();
//////                        isLoading = false;
//////                        Looper.loop();
////                        setIsOperating(false);
////                        if(mOperationType != Intents.FETCH_FILE_LIST)
////                        {
////                            mSearchFileIndex = getFilesWithKeyInfo(mFileName);
////                        }
////
////                        if(mOnResponeListener!=null)
////                        {
////                            mOnResponeListener.onResponeCallback(0, mList, mSearchFileIndex, mOperationType);
////                        }
////                    }
////                }
////            }
////        });
//    }

    private int getFilesWithKeyInfo(String keyStr) {
        for (int i = 0; i < mList.size(); i++) {
            HTMLFile videoFile = mList.get(i);
            if (videoFile.getKeyString() != null && keyStr.equals(videoFile.getKeyString())) {
                return i;
            }
        }
        return -1;
    }
}
