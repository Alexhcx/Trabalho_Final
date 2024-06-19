package com.example.trabalhofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.trabalhofinal.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = findViewById(R.id.container);

        AdsServices.loadBannerAds(container,LoginActivity.this);
        AdsServices.loadFullscreenADS(LoginActivity.this);

        binding.loginBtn.setOnClickListener(view -> {

            String email = binding.emailEdittext.getText().toString();
            String password = binding.passwordEdittext.getText().toString();

            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                binding.emailEdittext.setError("Email inválido");
                return;
            }

            if (password.length() < 6) {
                binding.passwordEdittext.setError("a senha é menor que 6 caracteres");
                return;
            }
            loginWithFirebase(email, password);
        });

        binding.gotoSignupBtn.setOnClickListener(view -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void loginWithFirebase(String email, String password) {
        setInProgress(true);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    setInProgress(false);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    AdsServices.showFullscreenADS(LoginActivity.this);
                    Toast.makeText(getApplicationContext(), "Login realizado com sucesso.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    setInProgress(false);
                    Toast.makeText(getApplicationContext(), "Login falhou, tente novamente.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            binding.loginBtn.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.loginBtn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}