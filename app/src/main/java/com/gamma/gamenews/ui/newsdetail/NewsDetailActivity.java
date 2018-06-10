package com.gamma.gamenews.ui.newsdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamma.gamenews.R;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.utils.DependencyContainer;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {

    News mNew;
    ImageView coverImage, btnFavorite;
    TextView txtTitle, txtSubtitle, txtGame, txtBody;
    CollapsingToolbarLayout collapsingToolbar;
    final String TAG = "NewsDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        findViews();

        Intent intent = getIntent();
        String newid = intent.getStringExtra("id");

        DetailViewModelFactory factory = DependencyContainer.getDetailViewModelFactory(this, newid);
        NewsDetailViewModel model = ViewModelProviders.of(this, factory).get(NewsDetailViewModel.class);

        //Log.d(TAG, "onCreate: Noticia: "+mNew.getTitle());

        model.getNew().observe(this, mNew -> {
            Log.d(TAG, "onCreate: Estoy en el observer");
            if(mNew != null) {
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
            } else Log.d(TAG, "onCreate: La noticia es nula alv");
        });
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setTransitionName(findViewById(R.id.appbar_layout), "transition");

        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
    }
}
