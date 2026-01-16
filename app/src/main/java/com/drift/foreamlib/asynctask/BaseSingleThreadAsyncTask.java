package com.drift.foreamlib.asynctask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class BaseSingleThreadAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
	//TASK controller
		private static ExecutorService SINGLE_TASK_EXECUTOR;  
	    private static ExecutorService LIMITED_TASK_EXECUTOR;  
	    private static ExecutorService FULL_TASK_EXECUTOR;  
	      
	    static {  
	        SINGLE_TASK_EXECUTOR = (ExecutorService) Executors.newSingleThreadExecutor();  
	        LIMITED_TASK_EXECUTOR = (ExecutorService) Executors.newFixedThreadPool(5);  
	        FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();  
	       // FULL_TASK_EXECUTOR.shutdownNow();
	    };  
	    /**
	     * Old execute() function only run 5 max threads.
	     * This Function no limitaion.
	     * @param params
	     */
	    @SuppressLint("NewApi")
		public void executeOnThreadPool(
				Params... params) {
            if (Build.VERSION.SDK_INT < 4) {
                // Thread pool size is 1
                 execute(params);
            } else if (Build.VERSION.SDK_INT < 11) {
                // The execute() method uses a thread pool
            	execute(params);
            }else{
            	executeOnExecutor(SINGLE_TASK_EXECUTOR,params);
            }
		}
}
