//package com.example.demo.Volunteer.Preferences;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("preferences")
//public class PreferencesController {
//
//    private final PreferencesRepository preferencesRepository;
//
//    public PreferencesController(PreferencesRepository preferencesRepository) {
//        this.preferencesRepository = preferencesRepository;
//    }
//
//    @GetMapping
//    public List<Preferences> getPreferences() {
//        return preferencesRepository.findAll();
//    }
//}
