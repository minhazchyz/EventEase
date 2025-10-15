package com.example.manageprogramme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final long HARD_TIMEOUT_MS = 20_000;
    private static final long DB_SOFT_TIMEOUT_MS = 3_000;

    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;

    private DatabaseReference usersRef;
    private FirebaseAuth auth;

    private boolean isSubmitting = false;
    private boolean hasNavigated = false;   // ← late toast/timeout থামাবে
    private Runnable hardWatchdog = null;
    private Runnable dbSoftTimeout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try { FirebaseApp.initializeApp(this); } catch (Exception ignored) {}

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedEmail = prefs.getString("email", null);
        String savedName = prefs.getString("name", null);
        String savedUsername = prefs.getString("username", null);
        if (!TextUtils.isEmpty(savedEmail)) {
            startMain(savedName, savedUsername, savedEmail);
            return;
        }

        setContentView(R.layout.activity_login);
        bindViews();
        initFirebase();

        loginEmail.setHint("Email");

        loginButton.setOnClickListener(v -> {
            hideKeyboard();
            attemptLogin();
        });
        signupRedirectText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        clearTimers();
    }

    private void bindViews() {
        loginEmail = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();

        FirebaseDatabase db;
        try {
            String cfgUrl = getString(R.string.firebase_db_url);
            if (TextUtils.isEmpty(cfgUrl)
                    || cfgUrl.contains("<project-id>")
                    || cfgUrl.startsWith("https://your-project-id")) {
                db = FirebaseDatabase.getInstance();
                Log.w(TAG, "Using default RTDB instance (fallback). Set firebase_db_url.");
            } else {
                db = FirebaseDatabase.getInstance(cfgUrl.trim());
            }
        } catch (Exception e) {
            Log.e(TAG, "RTDB init with URL failed, fallback: " + e.getMessage());
            db = FirebaseDatabase.getInstance();
        }

        usersRef = db.getReference("users");

        try {
            db.getReference(".info/connected")
                    .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                        @Override public void onDataChange(DataSnapshot s) {
                            Log.d(TAG, "RTDB .info/connected = " + s.getValue());
                        }
                        @Override public void onCancelled(@NonNull com.google.firebase.database.DatabaseError e) {
                            Log.w(TAG, "connected-ref cancelled: " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.w(TAG, "connected listener attach failed: " + e.getMessage());
        }
    }

    private void setLoading(boolean loading) {
        isSubmitting = loading;
        loginButton.setEnabled(!loading);
        loginButton.setText(loading ? "Please wait..." : "Login");
        if (!loading) clearTimers();
    }

    private void armHardWatchdog() {
        if (hardWatchdog != null) loginButton.removeCallbacks(hardWatchdog);
        hardWatchdog = () -> {
            if (isSubmitting && !hasNavigated) {
                setLoading(false);
                toastLong("⏱️ Login timed out.\n• Check internet\n• Check google-services.json\n• (Emulator? update Play Services)");
                Log.w(TAG, "Login hard-timeout fired");
            }
        };
        loginButton.postDelayed(hardWatchdog, HARD_TIMEOUT_MS);
    }

    private void armDbSoftTimeout(final String uid, final String email, final FirebaseUser fUser) {
        if (dbSoftTimeout != null) loginButton.removeCallbacks(dbSoftTimeout);
        dbSoftTimeout = () -> {
            if (isSubmitting && !hasNavigated) {
                Log.w(TAG, "DB soft-timeout fired, continuing with minimal profile");
                Map<String, Object> profile = buildMinimalProfile(uid, email, fUser);
                // ❌ কোনো টোস্ট দেখাবো না—সরাসরি এগোবো
                saveSessionAndGo(profile);
            }
        };
        loginButton.postDelayed(dbSoftTimeout, DB_SOFT_TIMEOUT_MS);
    }

    private void clearTimers() {
        if (hardWatchdog != null) {
            loginButton.removeCallbacks(hardWatchdog);
            hardWatchdog = null;
        }
        if (dbSoftTimeout != null) {
            loginButton.removeCallbacks(dbSoftTimeout);
            dbSoftTimeout = null;
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception ignored) {}
    }

    private boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network nw = cm.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(nw);
            return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            android.net.NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        }
    }

    private void attemptLogin() {
        if (isSubmitting) return;

        final String email = safeText(loginEmail);
        final String password = safeText(loginPassword);

        if (!isValidEmail(email)) {
            loginEmail.setError("Enter a valid email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Password can't be empty");
            return;
        }
        if (!hasInternet()) {
            toastLong("❌ No internet connection");
            return;
        }

        setLoading(true);
        armHardWatchdog();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        setLoading(false);
                        String msg = (task.getException() != null)
                                ? task.getException().getLocalizedMessage()
                                : "Auth failed";
                        loginPassword.setError("Invalid credentials");
                        toastLong("❌ " + msg);
                        Log.e(TAG, "signIn failed: " + msg);
                        return;
                    }

                    FirebaseUser fUser = auth.getCurrentUser();
                    if (fUser == null) {
                        setLoading(false);
                        toastLong("Login failed: user null");
                        Log.e(TAG, "FirebaseUser null after signIn");
                        return;
                    }

                    if (!fUser.isEmailVerified()) {
                        // Re-send verification (best-effort)
                        fUser.sendEmailVerification();
                        setLoading(false);
                        toastLong("⚠ Verify your email first. A new link was sent to " + email);
                        auth.signOut();
                        return;
                    }

                    final String uid = fUser.getUid();
                    final String finalEmail = (fUser.getEmail() != null ? fUser.getEmail() : email);

                    armDbSoftTimeout(uid, finalEmail, fUser);

                    usersRef.child(uid).get()
                            .addOnCompleteListener(dbTask -> {
                                if (!dbTask.isSuccessful()) {
                                    Log.w(TAG, "DB read failed: " + err(dbTask.getException()));
                                    // soft-timeout will continue
                                    return;
                                }

                                DataSnapshot snap = dbTask.getResult();
                                if (snap == null || !snap.exists()) {
                                    Map<String, Object> profile = buildMinimalProfile(uid, finalEmail, fUser);
                                    usersRef.child(uid).setValue(profile)
                                            .addOnSuccessListener(v -> saveSessionAndGo(profile))
                                            .addOnFailureListener(e ->
                                                    Log.e(TAG, "Profile create failed: " + e.getLocalizedMessage()));
                                } else {
                                    Map<String, Object> profile = (Map<String, Object>) snap.getValue();
                                    if (profile == null) profile = new HashMap<>();
                                    if (!profile.containsKey("email"))    profile.put("email", finalEmail);
                                    if (!profile.containsKey("username")) profile.put("username", finalEmail.split("@")[0]);
                                    saveSessionAndGo(profile);
                                }
                            })
                            .addOnFailureListener(e ->
                                    Log.w(TAG, "DB read error: " + e.getLocalizedMessage()));
                });
    }

    @NonNull
    private Map<String, Object> buildMinimalProfile(@NonNull String uid,
                                                    @NonNull String email,
                                                    @NonNull FirebaseUser fUser) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("uid", uid);
        profile.put("name", fUser.getDisplayName() != null ? fUser.getDisplayName() : "");
        profile.put("username", email.split("@")[0]);
        profile.put("email", email);
        profile.put("createdAt", System.currentTimeMillis());
        return profile;
    }

    private void saveSessionAndGo(@NonNull Map<String, Object> profile) {
        // UI/timers বন্ধ করেই ন্যাভিগেট করি—কোনো success toast নেই
        setLoading(false);
        hasNavigated = true;   // ← আর কোনো watchdog/soft-timeout ট্রিগার করবে না

        String name     = String.valueOf(profile.getOrDefault("name", ""));
        String username = String.valueOf(profile.getOrDefault("username", ""));
        String email    = String.valueOf(profile.getOrDefault("email", ""));

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        prefs.edit()
                .putString("username", username)
                .putString("name", name)
                .putString("email", email)
                .apply();

        startMain(name, username, email);
    }

    @MainThread
    private void startMain(String name, String username, String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private static String safeText(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void toastLong(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
    private static String err(Exception e) { return (e != null ? e.getLocalizedMessage() : "Unknown"); }
}
