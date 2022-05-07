package com.suli.rk.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.suli.rk.R;
import com.suli.rk.databinding.FragmentDashboardBinding;

import java.sql.Array;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = binding.textDashboard;
        textView.setText("Your saved ending times are: \n"+ getEndingTimesString());


        Spinner spinner = binding.spinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        EditText timeTextBox = (EditText) binding.editTextTime;







        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        Button saveButton = (Button) binding.button3;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = timeTextBox.getText().toString();
                String splitinput [] = input.split(":");
                int hour = 1;
                int minute = 1;
                if(Integer.parseInt(splitinput[0]) < 24 && Integer.parseInt(splitinput[1])<60){
                    hour = Integer.parseInt(splitinput[0]);
                    minute = Integer.parseInt(splitinput[1]);


                }
                else{
                    Toast.makeText(getContext(), "The time you entered is invalid. Enter it like this 15:30", Toast.LENGTH_LONG).show();
                    return;
                }


                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("OrakPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    int daynumber = spinner.getSelectedItemPosition()+1;
                    editor.putInt(daynumber+"h",hour);
                    editor.putInt(daynumber+"m",minute);
                    editor.commit();
                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
                    System.out.println("Saved "+hour+" and "+minute+" for day "+daynumber);
                    textView.setText("Your saved ending times are: \n"+ getEndingTimesString());



            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String getEndingTimesString(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("OrakPrefs", Context.MODE_PRIVATE);
        String[] days = {"Monday","Tuesday", "Wednesday", "Thursday", "Friday"};
        String result = "";
        for(int i =1; i<6; i++){
            if(+sharedPreferences.getInt(i+"h",-1)==-1){
                result = result + days[i-1]+": "+16+":"+"00"+"\n";
                continue;
            }
            result = result + days[i-1]+": "+sharedPreferences.getInt(i+"h",-1)+":"+sharedPreferences.getInt(i+"m",-1)+"\n";
        }
        return result;
    }
}