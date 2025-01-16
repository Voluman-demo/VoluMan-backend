package com.example.demo.Volunteer.Preferences;

import com.example.demo.Action.Version;
import com.example.demo.Schedule.Decision;
import com.example.demo.Action.Action;
import org.springframework.stereotype.Service;

@Service
public class PreferencesService {


    private final PreferencesRepository preferencesRepository;

    public PreferencesService(PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    public void addPreferences(Preferences preferences) {
        preferencesRepository.save(preferences);
    }


    public void removeActionFromOtherPreferences(Version action, Long preferencesId, Decision decision) {
        Preferences pref = preferencesRepository.findById(preferencesId).orElse(null);

        if (pref != null) {
            if (decision == Decision.S){
                pref.getW().remove(action);
                pref.getR().remove(action);
            }
            else if (decision == Decision.W){
                pref.getS().remove(action);
                pref.getW().remove(action);
            } else if (decision == Decision.R) {
                pref.getW().remove(action);
                pref.getS().remove(action);
            }
            preferencesRepository.save(pref);
        }
    }
}
