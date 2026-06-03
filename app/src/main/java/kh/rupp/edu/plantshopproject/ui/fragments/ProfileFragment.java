package kh.rupp.edu.plantshopproject.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import kh.rupp.edu.plantshopproject.R;
import kh.rupp.edu.plantshopproject.session.SessionManager;
import kh.rupp.edu.plantshopproject.ui.LoginActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager session = new SessionManager(requireContext());

        TextView tvUsername = view.findViewById(R.id.tv_username);
        TextView tvEmail    = view.findViewById(R.id.tv_email);
        Button btnLogout    = view.findViewById(R.id.btn_logout);

        tvUsername.setText(session.getUsername());
        tvEmail.setText(session.getUsername() + "@planshop.com");

        btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }
}
