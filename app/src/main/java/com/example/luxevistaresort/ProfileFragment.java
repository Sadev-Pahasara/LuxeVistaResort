package com.example.luxevistaresort;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // ✅ Show user's full name
        TextView tvUser = v.findViewById(R.id.tvUser); // <-- make sure this id exists in fragment_profile.xml
        SharedPreferences sp = requireActivity().getSharedPreferences("USER_SESSION", 0);
        String fullName = sp.getString("FULL_NAME", "User");
        if (tvUser != null) tvUser.setText(fullName);

        return v;
    }
}