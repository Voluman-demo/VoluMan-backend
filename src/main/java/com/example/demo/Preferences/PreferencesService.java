package com.example.demo.Preferences;

import com.example.demo.Schedule.Decision;
import com.example.demo.action.Action;
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


    public void removeActionFromOtherPreferences(Action action, Long preferencesId, Decision decision) {
        Preferences pref = preferencesRepository.findById(preferencesId).orElse(null);

        if (pref != null) {
            if (decision == Decision.T){
                pref.getR().remove(action);
                pref.getN().remove(action);
            }
            else if (decision == Decision.R){
                pref.getT().remove(action);
                pref.getN().remove(action);
            } else if (decision == Decision.N) {
                pref.getT().remove(action);
                pref.getR().remove(action);
            }
            preferencesRepository.save(pref);
        }
    }
}
