package com.gamma.gamenews.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.R;
import com.squareup.picasso.Picasso;

public class NewsDetailFragment extends Fragment {

    News mNew;
    ImageView coverImage, btnFavorite;
    TextView txtTitle, txtSubtitle, txtGame, txtBody;
    final String TAG = "NewsDetail";
    public NewsDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        NewsDetailViewModel model = ViewModelProviders.of(getActivity()).get(NewsDetailViewModel.class);
        model.getNew().observe(this, mNew -> {
            if(mNew != null) {
                if (mNew.getCoverImage() != null) {
                    Picasso.get().load(mNew.getCoverImage())
                            .error(R.drawable.no_image)
                            .placeholder(R.drawable.no_image)
                            .into(coverImage);
                } else
                    coverImage.setImageResource(R.drawable.no_image);

                if (mNew.getTitle() != null)
                    txtTitle.setText(mNew.getTitle().trim());
                else
                    txtTitle.setText(getResources().getString(R.string.no_title_available));

                if (mNew.getDescription() != null)
                    txtSubtitle.setText(mNew.getDescription().trim());
                else
                    txtSubtitle.setText(getResources().getString(R.string.no_description_available));

                if (mNew.getGame() != null)
                    txtGame.setText(mNew.getGame().trim());
                else
                    txtGame.setText(getResources().getString(R.string.no_game));

                if (mNew.getBody() != null)
                    txtBody.setText(mNew.getBody().trim());
                else
                    txtBody.setText(getResources().getString(R.string.no_game));
            } else Log.d(TAG, "onActivityCreated: La noticia es nula alv");
        });

        //getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_detail, container, false);
        Log.d(TAG, "onCreateView: ESTOY EN EL PUTO ON CREATEVIEW");
        coverImage = v.findViewById(R.id.img_cover);
        btnFavorite = v.findViewById(R.id.btn_favorite);
        txtTitle = v.findViewById(R.id.txt_title);
        txtSubtitle = v.findViewById(R.id.txt_subtitle);
        txtGame = v.findViewById(R.id.txt_game);
        txtBody = v.findViewById(R.id.txt_body);
        return v;
    }

}
