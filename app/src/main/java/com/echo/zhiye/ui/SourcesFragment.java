package com.echo.zhiye.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.echo.zhiye.R;
import com.echo.zhiye.data.NewsSource;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.HashSet;
import java.util.Set;

public class SourcesFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s) { return i.inflate(R.layout.fragment_sources,c,false); }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s) {
        LinearLayout domestic=v.findViewById(R.id.domestic_sources), international=v.findViewById(R.id.international_sources), finance=v.findViewById(R.id.finance_sources);
        Set<String> shown=new HashSet<>();
        for(NewsSource source:NewsSource.all()) {
            if(!shown.add(source.name)) continue;
            MaterialCardView card=(MaterialCardView)LayoutInflater.from(requireContext()).inflate(R.layout.item_source,null,false);
            ((TextView)card.findViewById(R.id.source_name)).setText(source.name);
            ((TextView)card.findViewById(R.id.source_description)).setText(source.description);
            ((TextView)card.findViewById(R.id.source_status)).setText(source.type);
            MaterialButton open=card.findViewById(R.id.source_open);
            open.setOnClickListener(x->startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(source.website))));
            MaterialButton read=card.findViewById(R.id.source_read);
            read.setVisibility(source.hasFeed()?View.VISIBLE:View.GONE);
            read.setOnClickListener(x->{ Bundle b=new Bundle(); b.putString("source",source.name); Navigation.findNavController(v).navigate(R.id.searchFragment,b); });
            (source.region.equals("国内媒体")?domestic:source.region.equals("财经媒体")?finance:international).addView(card);
        }
    }
}
