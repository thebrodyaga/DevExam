package com.example.win10.devexam.loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.win10.devexam.api.ApiFactory;
import com.example.win10.devexam.api.ITechService;
import com.example.win10.devexam.dummy.DummyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by Win10 on 22.07.2017.
 */

public class ITechLoader extends AsyncTaskLoader<List<DummyContent>> {
    public ITechLoader(Context context) {
        super(context);
    }

    @Override
    public List<DummyContent> loadInBackground() {
        List<DummyContent> answer = null;
        ITechService service = ApiFactory.getITechService();
        Log.d("My_log", "loadInBackground");
        Response<List<DummyContent>> call = null;
        try {
            call = service.getDataList().execute();
            answer = new ArrayList<>();
            answer.addAll(call.body());
        } catch (IOException e) {
            e.printStackTrace();
            return answer;
        }
        return answer;
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    public void deliverResult(List<DummyContent> data) {
        super.deliverResult(data);
    }
}
