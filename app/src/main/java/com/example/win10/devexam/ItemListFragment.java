package com.example.win10.devexam;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.win10.devexam.loader.ITechLoader;
import com.example.win10.devexam.dummy.DummyContent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ItemListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<DummyContent>> {
    private static final int LOADER_ID = 1;
    private List<DummyContent> list;
    private CustomAdapter adapter;
    private Timer mTimer;
    private ItemListFragment listFragment;
    private RecyclerView recyclerView;
    private Snackbar snackbar;
    private static final long timeToRefresh = 10000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        Log.d("MyLog", "onCreateView");
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.my_coordinator_layout);
        setHasOptionsMenu(true);
        listFragment = this;
        list = new ArrayList<>();
        adapter = new CustomAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        snackbar = Snackbar
                .make(coordinatorLayout, "Нет соединения", Snackbar.LENGTH_LONG)
                .setAction("Повторить", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getLoaderManager().restartLoader(LOADER_ID, null, listFragment);
                    }
                });

        getLoaderManager().initLoader(LOADER_ID, null, this);
        startTimerTask(); //данные обновляет, если нужна другая реализация, сделаю
        return view;
    }

    private void startTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(LOADER_ID, null, listFragment);
            }
        }, timeToRefresh, timeToRefresh);
    }

    @Override
    public void onStop() {
        super.onStop();
        mTimer.cancel();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                startTimerTask();
                Log.d("MyLog", "action_refresh");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<DummyContent>> onCreateLoader(int id, Bundle args) {
        Log.d("MyLog", "onCreateLoader");
        return new ITechLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<DummyContent>> loader, List<DummyContent> data) {
        Log.d("MyLog", "onLoadFinished");
        if (data != null) {
            adapter.clear();
            adapter.addAll(data);
        } else {
            mTimer.cancel();
            snackbar.show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<DummyContent>> loader) {

    }

    private class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final List<DummyContent> mValues;

        CustomAdapter(List<DummyContent> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mTitleView.setText(mValues.get(position).getTitle());
            holder.mTextView.setText(mValues.get(position).getText());
            String imageUrl = mValues.get(position).getImage();
            if (!(imageUrl == null)) {
                Picasso.with(getActivity())
                        .load(imageUrl)
                        .error(R.mipmap.ic_error)
                        .fit().centerCrop()
                        .into(holder.picture, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.circular_progress.setVisibility(View.INVISIBLE);
                                holder.picture.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                holder.circular_progress.setVisibility(View.INVISIBLE);
                                holder.picture.setVisibility(View.VISIBLE);
                            }
                        });
            } else {
                holder.picture.setImageResource(R.mipmap.ic_error);
                holder.circular_progress.setVisibility(View.INVISIBLE);
                holder.picture.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        void clear() {
            mValues.clear();
        }

        // Add a list of items -- change to type used
        void addAll(List<DummyContent> list) {
            mValues.addAll(list);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView picture;
        TextView mTitleView;
        TextView mTextView;
        ProgressBar circular_progress;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            picture = (ImageView) itemView.findViewById(R.id.card_image);
            circular_progress = (ProgressBar) itemView.findViewById(R.id.card_progress);
            mTitleView = (TextView) view.findViewById(R.id.card_title);
            mTextView = (TextView) view.findViewById(R.id.card_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText() + "'";
        }
    }
}
