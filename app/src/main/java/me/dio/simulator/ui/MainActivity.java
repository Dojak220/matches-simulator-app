package me.dio.simulator.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.dio.simulator.R;
import me.dio.simulator.data.MatchesAPI;
import me.dio.simulator.databinding.ActivityMainBinding;
import me.dio.simulator.domain.Match;
import me.dio.simulator.ui.adapter.MatchesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MatchesAPI matchesAPI;
    private MatchesAdapter matchesAdapter  = new MatchesAdapter(Collections.emptyList());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupHttpClient();
        setupMatchesList();
        setupMatchesRefresh();
        setupFloatingButton();
    }

    private void setupHttpClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dojak220.github.io/match-simulator-api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        matchesAPI = retrofit.create(MatchesAPI.class);
    }

    private void setupMatchesList() {
        binding.rvMatches.setHasFixedSize(true);
        binding.rvMatches.setLayoutManager(new LinearLayoutManager(this));

        binding.rvMatches.setAdapter(matchesAdapter);
        getMatches();
    }

    private void getMatches() {
        binding.srlMatches.setRefreshing(true);
        matchesAPI.getMatches().enqueue(handleCallback());
        binding.srlMatches.setRefreshing(false);
    }

    @NonNull
    private Callback<List<Match>> handleCallback() {
        return new Callback<List<Match>>() {
            @Override
            public void onResponse(@NonNull Call<List<Match>> call, @NonNull Response<List<Match>> response) {
                if(response.isSuccessful()) {
                    List<Match> matches = response.body();
                    assert matches != null;

                    matchesAdapter = new MatchesAdapter(matches);
                    binding.rvMatches.setAdapter(matchesAdapter);
                } else {
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Match>> call, @NonNull Throwable t) {
                showErrorMessage();
            }
        };
    }

    private void showErrorMessage() {
        Snackbar.make(binding.fabSimulate, R.string.error_api, Snackbar.LENGTH_LONG).show();
    }

    private void setupMatchesRefresh() {
        binding.srlMatches.setOnRefreshListener(this::getMatches);
    }

    private void setupFloatingButton() {

        binding.fabSimulate.setOnClickListener(view -> {
            view.animate().rotationBy(360).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Random random = new Random();
                    for (int i = 0; i < matchesAdapter.getItemCount(); i++) {
                        Match match = matchesAdapter.getMatches().get(i);
                        match.setHomeTeamScore(random.nextInt(match.getHomeTeam().getStrength() + 1));
                        match.setAwayTeamScore(random.nextInt(match.getAwayTeam().getStrength() + 1));
                        matchesAdapter.notifyItemChanged(i);
                    }
                }
            });
        });
        /// TODO: Create click event and simulate matches results
    }
}
