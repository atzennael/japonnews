package com.example.japonnews;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
import java.util.List;
//import com.example.japonnews.ImageAdapter;



public class FirstFragment extends Fragment {
    private ViewPager2 viewPager;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);

        // Lista de imágenes (pueden ser URLs o nombres de recursos locales)
        List<String> images = Arrays.asList(
                "https://i.pinimg.com/236x/f4/db/8a/f4db8a52c1b887bad46a301afa6b7167.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTVtzurk5_92_SS30O6Zgq89G06jLY2aC5B2w&s",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQraQ0F9c2NVwpSwoLcrCKvStaX5N_yd8EGXQ&s"
        );

        ImageAdapter adapter = new ImageAdapter(requireContext(), images);
        viewPager.setAdapter(adapter);

        // Auto desplazamiento (opcional)
        autoScroll(viewPager, images.size());
    }

    private void autoScroll(ViewPager2 viewPager, int size) {
        runnable = new Runnable() {
            int currentPage = 0;

            @Override
            public void run() {
                if (currentPage >= size) currentPage = 0;
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000); // Cambia cada 3 segundos
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable); // Detener el auto-scroll al salir
    }
}