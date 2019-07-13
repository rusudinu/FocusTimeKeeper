package com.codingshadows.focustimekeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;


public class SliderAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;


    SliderAdapter(Context context)
    {
        this.context = context;
    }


    public static int getSize(){
        return slide_headigns.length;
    }

    //arrays
    private int[] slide_images = {
            R.drawable.cslogowhitewhite,
            R.drawable.cslogowhitewhite,
            R.drawable.cslogowhitewhite,
            R.drawable.cslogowhitewhite,
            R.drawable.rarefocuscoin,
            R.drawable.cslogowhitewhite,
            R.drawable.cslogowhitewhite
    };

    private static String[] slide_headigns = {
            "Salut!",
            "Ce poti face in aplicatie?",
            "Cum functioneaza preset-urile?",
            "Daca o zi a lunii are un program diferit?",
            "Cum functioneaza cronometrul?",
            "Ce pot sa fac cu Focus Points?",
            "Cum functioneaza ecranul de <<Performanta>>?",
            "Stii cat timp ai pierdut cu acest tutorial?"
    };

    private String[] slide_descr = {
            "Ne bucura sa te vedem citind acest tutorial! \n Daca nu gasesti ceea ce te intereseaza aici, ne poti trimite un mesaj pe Facebook (@CodingShadows) sau un email (contact@codingshadows.com).",
            "Focus: Time Keeper te ajuta sa iti organizezi programul zilnic folosid preset-uri sau zile speciale. Pentru a modifica programul tau zilnic, apasa pe butonul <<Program nou>>, apoi selecteaza ziua si apasa pe + pentru a incepe sa adaugi activitati.",
            "Preset-urile te ajuta sa iti organizezi orice zi a lunii cu doar un click, atunci cand iti modifici programul. Iti poti seta un program individual pentru fiecare zi a saptamanii si apoi aplciatia il va aplica pentru fiecare zi corespunzatoare a lunii.",
            "Nici o problema! Poti seta un program special pentru anumite date din an. Pentru acele date, algoritmul nu va mai tine cont de preset si iti va arata programul special",
            "Cronometrul iregistreaza timpul pana cand tu deschizi o aplicatie <<interzisa>> precum Whatsapp, Facebook, Netflix, Reddit, etc.. In acel moment cronometrul se va opri si vei primi niste Focus Points in functie de cat timp ai rezistat!",
            "Cu ajutorul Focus Points poti sa iti cumperi monede de tipul celei de mai sus. Apoi te poti lauda prietenilor cu achizitiile tale!",
            "Ecranul de performanta masoara rata de completare a activitatilor tale dintr-o zi! Acesta se coloreaza diferit in functie de procentaj! Ideal ar fi sa reusesti sa il faci verde (ca pe Hulk!) in fiecare zi!",
            "In medie, utilizatorii acestie aplicatii pierd intre 3 si 4 minute cu acest tutorial! Speram ca de acum in colo nu vei mai pierdde timpul! \n Bafta!"
    };

    @Override
    public int getCount() {
        return slide_headigns.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView image = view.findViewById(R.id.slideImage);
        TextView slideTitle = view.findViewById(R.id.slideTitle);
        TextView slideDescription = view.findViewById(R.id.slideDescription);

        image.setImageResource(slide_images[position]);
        slideTitle.setText(slide_headigns[position]);
        slideDescription.setText(slide_descr[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
