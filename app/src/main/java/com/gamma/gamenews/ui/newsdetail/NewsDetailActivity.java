package com.gamma.gamenews.ui.newsdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamma.gamenews.AppExecutors;
import com.gamma.gamenews.R;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.data.network.NetworkDataSource;
import com.gamma.gamenews.utils.DependencyContainer;
import com.gamma.gamenews.utils.SharedPreference;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {

    ImageView coverImage, btnFavorite;
    TextView txtTitle, txtSubtitle, txtGame, txtBody;
    String newid;
    CollapsingToolbarLayout collapsingToolbar;
    private static final String TAG = "GN:NewsDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        newid = intent.getStringExtra("id");

        findViews();

        Log.d(TAG, "onCreate: Getting the ModelFactory");
        DetailViewModelFactory factory = DependencyContainer.getDetailViewModelFactory(this, newid);
        NewsDetailViewModel model = ViewModelProviders.of(this, factory).get(NewsDetailViewModel.class);

        model.getNew().observe(this, mNew -> {
            if(mNew != null) {
                updateUI(mNew);
            } else Log.d(TAG, "observer: Data is null");
        });
    }

    private void updateUI(News mNew){
        collapsingToolbar.setTitle(mNew.getGame().toUpperCase());
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

        if(SharedPreference.checkFavorite(newid)){
            btnFavorite.setTag("y");
            btnFavorite.setImageResource(R.drawable.ic_favorites);
        } else {
            btnFavorite.setTag("n");
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }

    }

    private void findViews(){
        coverImage = findViewById(R.id.img_cover);
        btnFavorite = findViewById(R.id.btn_favorite);
        txtTitle = findViewById(R.id.txt_title);
        txtSubtitle = findViewById(R.id.txt_subtitle);
        txtGame = findViewById(R.id.txt_game);
        txtBody = findViewById(R.id.txt_body);
        setSupportActionBar(findViewById(R.id.toolbar));
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        btnFavorite.setOnClickListener(v -> {
            NetworkDataSource.getInstance(getApplicationContext(), AppExecutors.getInstance()).setFavorite(v,newid);
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setTransitionName(findViewById(R.id.appbar_layout), "transition");

        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
    }
}
