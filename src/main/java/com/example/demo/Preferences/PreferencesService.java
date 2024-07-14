package com.example.demo.Preferences;

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
}
