package com.echo.zhiye.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsEntity;
import com.echo.zhiye.data.NewsSource;
import com.echo.zhiye.viewmodel.NewsViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private NewsViewModel viewModel;
    private NewsAdapter adapter;
    private SwipeRefreshLayout refresh;
    private String topic = "全部";
    private String source = "全部";
    private View root;
    private View tabletPane;
    private String tabletSelectedId;
    private LiveData<List<NewsEntity>> active;
    private Observer<List<NewsEntity>> activeObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        root = view;
        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        TextView editionDate = view.findViewById(R.id.edition_date);
        editionDate.setText(new SimpleDateFormat("yyyy年MM月dd日  EEEE", Locale.CHINA)
                .format(new Date()));

        RecyclerView list = view.findViewById(R.id.news_recycler);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NewsAdapter(new NewsAdapter.Listener() {
            @Override
            public void open(NewsEntity article) {
                viewModel.markRead(article.id);
                if (tabletPane != null) {
                    showTabletDetail(article);
                } else {
                    Bundle args = new Bundle();
                    args.putString("articleId", article.id);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.articleDetailFragment, args);
                }
            }

            @Override
            public void bookmark(NewsEntity article) {
                viewModel.setBookmark(article.id, article.isBookmarked);
                Toast.makeText(requireContext(),
                        article.isBookmarked ? "已加入收藏" : "已取消收藏",
                        Toast.LENGTH_SHORT).show();
            }
        }, true, true);
        list.setAdapter(adapter);

        ChipGroup chips = view.findViewById(R.id.topic_chips);
        chips.setSingleSelection(true);
        String[] topics = {"全部", "科技", "政治", "经济", "文化", "社会观察", "娱乐"};
        for (String value : topics) {
            Chip chip = new Chip(requireContext());
            chip.setText(value);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_bg);
            chip.setChipStrokeColorResource(R.color.rule);
            chip.setChipStrokeWidth(getResources().getDisplayMetrics().density);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text));
            chip.setCheckedIconVisible(false);
            chip.setOnClickListener(ignored -> {
                topic = value;
                observe();
            });
            chips.addView(chip);
            if ("全部".equals(value)) chip.setChecked(true);
        }

        Spinner sourceSpinner = view.findViewById(R.id.source_filter);
        List<String> sourceNames = new ArrayList<>();
        sourceNames.add("全部");
        for (NewsSource newsSource : NewsSource.all()) {
            if (!sourceNames.contains(newsSource.name)) sourceNames.add(newsSource.name);
        }
        sourceSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, sourceNames));
        sourceSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View item, int position, long id) {
                source = sourceNames.get(position);
                observe();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        view.findViewById(R.id.search_button)
                .setOnClickListener(ignored -> Navigation.findNavController(view).navigate(R.id.searchFragment));
        view.findViewById(R.id.refresh_button).setOnClickListener(ignored -> refreshNews());
        view.findViewById(R.id.home_topics_all)
                .setOnClickListener(ignored -> Navigation.findNavController(view).navigate(R.id.topicsFragment));
        view.findViewById(R.id.source_directory_button)
                .setOnClickListener(ignored -> Navigation.findNavController(view).navigate(R.id.sourcesFragment));

        refresh = view.findViewById(R.id.swipe_refresh);
        refresh.setOnRefreshListener(this::refreshNews);
        refresh.setColorSchemeResources(R.color.accent);

        tabletPane = view.findViewById(R.id.tablet_detail_pane);
        if (tabletPane != null) tabletPane.setVisibility(View.VISIBLE);

        observe();
        viewModel.seedSamples();
    }

    private void observe() {
        if (active != null && activeObserver != null) active.removeObserver(activeObserver);
        active = viewModel.filtered(topic, source);
        activeObserver = items -> {
            adapter.submit(items);
            boolean empty = items == null || items.isEmpty();
            root.findViewById(R.id.empty_state).setVisibility(empty ? View.VISIBLE : View.GONE);
            if (tabletPane != null) {
                if (empty) {
                    tabletPane.setVisibility(View.GONE);
                    tabletSelectedId = null;
                } else {
                    NewsEntity selected = null;
                    if (tabletSelectedId != null) {
                        for (NewsEntity item : items) {
                            if (tabletSelectedId.equals(item.id)) {
                                selected = item;
                                break;
                            }
                        }
                    }
                    showTabletDetail(selected == null ? items.get(0) : selected);
                }
            }
        };
        active.observe(getViewLifecycleOwner(), activeObserver);
    }

    private void showTabletDetail(NewsEntity article) {
        if (article == null || tabletPane == null) return;
        tabletSelectedId = article.id;
        tabletPane.setVisibility(View.VISIBLE);

        ((TextView) tabletPane.findViewById(R.id.tablet_detail_source))
                .setText(article.topic == null ? "" : article.topic);
        ((TextView) tabletPane.findViewById(R.id.tablet_detail_title)).setText(article.title);
        ((TextView) tabletPane.findViewById(R.id.tablet_detail_summary))
                .setText(article.summary == null ? "" : article.summary);

        String date = article.publishedAt > 0
                ? new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(new Date(article.publishedAt))
                : "时间未提供";
        ((TextView) tabletPane.findViewById(R.id.tablet_detail_meta))
                .setText(article.sourceName + "  ·  " + date);

        ImageView image = tabletPane.findViewById(R.id.tablet_detail_image);
        if (article.imageUrl != null && !article.imageUrl.isEmpty()) {
            image.setVisibility(View.VISIBLE);
            Glide.with(this).load(article.imageUrl).centerCrop()
                    .placeholder(R.drawable.bg_image_placeholder)
                    .error(R.drawable.bg_image_placeholder).into(image);
        } else {
            Glide.with(this).clear(image);
            image.setVisibility(View.GONE);
        }

        boolean external = !article.isLocal && !"知页样例".equals(article.sourceName);
        TextView body = tabletPane.findViewById(R.id.tablet_detail_body);
        boolean hasSeparateBody = article.body != null && !article.body.trim().isEmpty()
                && !article.body.equals(article.summary);
        body.setVisibility(hasSeparateBody ? View.VISIBLE : View.GONE);
        body.setText(hasSeparateBody ? article.body : "");
        TextView notice = tabletPane.findViewById(R.id.tablet_detail_notice);
        notice.setVisibility(external ? View.VISIBLE : View.GONE);
        notice.setText("当前展示媒体订阅源提供的摘要。阅读全文请前往原媒体页面。");

        tabletPane.findViewById(R.id.tablet_open_detail).setOnClickListener(ignored -> {
            Bundle args = new Bundle();
            args.putString("articleId", article.id);
            Navigation.findNavController(requireView()).navigate(R.id.articleDetailFragment, args);
        });
    }

    private void refreshNews() {
        refresh.setRefreshing(true);
        viewModel.refresh((newArticles, failedFeeds) -> {
            if (!isAdded()) return;
            refresh.setRefreshing(false);

            int configuredFeeds = 0;
            for (NewsSource newsSource : NewsSource.all()) {
                if (newsSource.hasFeed()) configuredFeeds++;
            }

            String message;
            if (configuredFeeds > 0 && failedFeeds >= configuredFeeds) {
                message = "订阅源暂不可用，已保留本地缓存";
            } else if (newArticles > 0) {
                message = "新增 " + newArticles + " 条新闻";
            } else {
                message = "订阅源已检查，暂无新文章";
            }
            if (failedFeeds > 0 && failedFeeds < configuredFeeds) {
                message += "；" + failedFeeds + " 个订阅源暂不可用";
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
    }
}
