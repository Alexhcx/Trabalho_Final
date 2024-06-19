package com.example.trabalhofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.trabalhofinal.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    LinearLayout container3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container3 = findViewById(R.id.container3);

        AdsServices.loadBannerAds(container3,SignupActivity.this);

        binding.createAccountBtn.setOnClickListener(view -> {
            String email = binding.emailEdittext.getText().toString();
            String password = binding.passwordEdittext.getText().toString();
            String confirmPassword = binding.confirmPasswordEdittext.getText().toString();

            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                binding.emailEdittext.setError("Email inválido");
                return;
            }

            if (password.length() < 6) {
                binding.passwordEdittext.setError("a senha é menor que 6 caracteres");
                return;
            }

            if (!password.equals(confirmPassword)) {
                binding.confirmPasswordEdittext.setError("As senhas não conferem");
                return;
            }

            createAccountWithFirebase(email, password);
        });

        binding.gotoLoginBtn.setOnClickListener(view -> finish());
    }

    private void createAccountWithFirebase(String email, String password) {
        setInProgress(true);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    setInProgress(false);
                    Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setInProgress(false);
                    Toast.makeText(getApplicationContext(), "Create account failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            binding.createAccountBtn.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.createAccountBtn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}