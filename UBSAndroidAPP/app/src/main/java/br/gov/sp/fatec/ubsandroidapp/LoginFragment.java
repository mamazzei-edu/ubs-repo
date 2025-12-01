package br.gov.sp.fatec.ubsandroidapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import br.gov.sp.fatec.ubsandroidapp.databinding.FragmentLoginBinding;
import br.gov.sp.fatec.ubsandroidapp.dtos.LoginRequest;
import br.gov.sp.fatec.ubsandroidapp.dtos.LoginResponse;
import br.gov.sp.fatec.ubsandroidapp.network.NetworkClient;
import br.gov.sp.fatec.ubsandroidapp.services.AuthService;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private String savedHostIp;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe DataStore for host IP
        if (getActivity() instanceof MainActivity) {
            RxDataStore<Preferences> dataStore = ((MainActivity) getActivity()).getDataStore();
            Preferences.Key<String> key = PreferencesKeys.stringKey("UBSAuth");

            disposable.add(dataStore.data()
                    .map(prefs -> prefs.get(key) != null ? prefs.get(key) : "")
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            ip -> {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        savedHostIp = ip;
                                        Log.d("LoginFragment", "Host IP retrieved: " + savedHostIp);
                                        updateLoginButtonState();
                                    });
                                }
                            },
                            throwable -> Log.e("LoginFragment", "Error reading DataStore", throwable)));
        }

        binding.login.setOnClickListener(v -> performLogin());
    }

    private void updateLoginButtonState() {
        if (savedHostIp != null && !savedHostIp.isEmpty()) {
            binding.login.setEnabled(true);
        }
    }

    private void performLogin() {
        String username = binding.username.getText().toString();
        String password = binding.password.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (savedHostIp == null || savedHostIp.isEmpty()) {
            Toast.makeText(getContext(), "Host IP not found. Please wait for service discovery.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String baseUrl = "http://" + savedHostIp + ":8080/";
        AuthService authService = NetworkClient.getInstance().getAuthService(baseUrl);
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Disable button while loading
        binding.login.setEnabled(false);

        authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                binding.login.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("LoginFragment", "Login successful. Token: " + response.body().getToken());
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_LoginFragment_to_SecondFragment);
                } else {
                    Toast.makeText(getContext(), "Login failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                binding.login.setEnabled(true);
                Toast.makeText(getContext(), "Login error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginFragment", "Login error", t);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        binding = null;
    }
}
