package kh.rupp.edu.plantshopproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.api.ApiClient;
import kh.rupp.edu.plantshopproject.model.LoginRequest;
import kh.rupp.edu.plantshopproject.model.LoginResponse;
import kh.rupp.edu.plantshopproject.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private SessionManager sessionManager;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        sessionManager = new SessionManager(this);

        etEmail  = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin   = findViewById(R.id.btn_login);

//        TextView tabLogin    = findViewById(R.id.tab_login);
//        TextView tabRegister = findViewById(R.id.tab_register);
        TextView tvGoRegister = findViewById(R.id.tv_go_register);

//        tabLogin.setOnClickListener(v -> switchMode(true, tabLogin, tabRegister));
//        tabRegister.setOnClickListener(v -> switchMode(false, tabLogin, tabRegister));
//        tvGoRegister.setOnClickListener(v -> switchMode(!isLoginMode, tabLogin, tabRegister));

        btnLogin.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            doLogin(email, password);
        });
    }

    private void switchMode(boolean loginMode, TextView tabLogin, TextView tabRegister) {
        isLoginMode = loginMode;
        if (loginMode) {
            tabLogin.setBackgroundResource(R.drawable.bg_tab_selected);
            tabRegister.setBackgroundResource(android.R.color.transparent);
            btnLogin.setText("Sign In");
        } else {
            tabRegister.setBackgroundResource(R.drawable.bg_tab_selected);
            tabLogin.setBackgroundResource(android.R.color.transparent);
            btnLogin.setText("Sign Up");
        }
    }

    private void doLogin(String username, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Please wait...");

        ApiClient.getService().login(new LoginRequest(username, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText(isLoginMode ? "Sign In" : "Sign Up");

                        if (response.isSuccessful() && response.body() != null) {
                            sessionManager.saveSession(
                                    response.body().getToken(), username);
                            Toast.makeText(LoginActivity.this,
                                    "Welcome to PlanShop! 🌿", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,
                                    MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Wrong credentials. Try: mor_2314 / 83r5^_",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText(isLoginMode ? "Sign In" : "Sign Up");
                        Toast.makeText(LoginActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
