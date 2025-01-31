package com.example.demo.Action;

import com.example.demo.Action.ActionDto.ActionRequest;
import com.example.demo.Action.ActionDto.DescriptionRequest;
import com.example.demo.Action.ActionDto.RoleRequest;
import com.example.demo.Model.Errors;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActionService implements Actions {
    private final ActionRepository actionRepository;

    private final VolunteerRepository volunteerRepository;

    public ActionService(ActionRepository actionRepository, VolunteerRepository volunteerRepository) {
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public Long create() {
        Action action = new Action();
        actionRepository.save(action);
        return action.getActionId();
    }

    @Override
    public Errors remove(Long actionId) {
        if (actionRepository.existsById(actionId)) {
            actionRepository.deleteById(actionId);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors setBeg(Long actionId, LocalDate beginDate) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            action.setBegin(beginDate);
            actionRepository.save(action);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors setEnd(Long actionId, LocalDate endDate) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            action.setEnd(endDate);
            actionRepository.save(action);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }



    @Override
    public Errors setDesc(Long actionId, Lang language, DescriptionRequest descriptionRequest) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();

            // Znajdź istniejący opis w danym języku
            Description description = action.getDescr().stream()
                    .filter(desc -> desc.getLang() == language)
                    .findFirst()
                    .orElse(null);

            if (description == null) {
                // Jeśli opis nie istnieje, utwórz nowy i przypisz do akcji
                description = new Description();
                description.setLang(language);
                description.setAction(action);
                action.getDescr().add(description);
            }

            // Aktualizuj tylko dane zawarte w żądaniu
            if (descriptionRequest.getBegin() != null) {
                description.setBegin(descriptionRequest.getBegin());
            }
            if (descriptionRequest.getEnd() != null) {
                description.setEnd(descriptionRequest.getEnd());
            }
            if (descriptionRequest.getFullName() != null) {
                description.setFullName(descriptionRequest.getFullName());
            }
            if (descriptionRequest.getShortName() != null) {
                description.setShortName(descriptionRequest.getShortName());
            }
            if (descriptionRequest.getPlace() != null) {
                description.setPlace(descriptionRequest.getPlace());
            }
            if (descriptionRequest.getAddress() != null) {
                description.setAddress(descriptionRequest.getAddress());
            }
            if (descriptionRequest.getDescription() != null) {
                description.setDescription(descriptionRequest.getDescription());
            }
            if (descriptionRequest.getHours() != null) {
                description.setHours(descriptionRequest.getHours());
            }
            if (descriptionRequest.getRoles() != null) {
                updateRoles(description, descriptionRequest.getRoles());
            }

            // Zapisz zmiany w repozytorium
            actionRepository.save(action);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }


    @Override
    public Errors remDesc(Long actionId, Lang language) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();

            // Znajdź opis w danym języku
            Description description = action.getDescr().stream()
                    .filter(desc -> desc.getLang() == language)
                    .findFirst()
                    .orElse(null);

            if (description != null) {
                // Ustaw opis jako nieważny i zapisz zmiany
                description.setValid(false);
                actionRepository.save(action);
                return Errors.SUCCESS;
            }
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Description getDesc(Long actionId, Lang language) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            return actionOpt.get().getDescr().stream().filter(description -> description.getLang().equals(language)).findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public ArrayList<Long> getAllIds() {
        return actionRepository.findAll().stream()
                .map(Action::getActionId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Description> getAllDesc(Lang language) {
        return actionRepository.findAll().stream()
                .map(action -> getDesc(action.getActionId(), language))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Errors setPref(Long actionId, String preference, Long volunteerId) {
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(volunteerId);
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent() && volunteerOpt.isPresent()) {
            Volunteer volunteer = volunteerOpt.get();
            Action action = actionOpt.get();
            switch (preference) {
                case "S" -> volunteer.getPreferences().getS().add(action);
                case "W" -> volunteer.getPreferences().getW().add(action);
                case "R" -> volunteer.getPreferences().getR().add(action);
                default -> volunteer.getPreferences().getU().add(action);
            }

            volunteerRepository.save(volunteer);
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }

    public List<Description> getPref(String preference, Long volunteerId) {
        Volunteer volunteer = volunteerRepository.getVolunteerByVolunteerId(volunteerId);
        Lang lang = volunteer.getLanguage();

        return switch (preference) {
            case "S" -> volunteer.getPreferences().getS().stream()
                    .flatMap(action -> action.getDescr().stream()
                            .filter(desc -> desc.getLang() == lang && desc.isValid()))
                    .collect(Collectors.toList());
            case "W" -> volunteer.getPreferences().getW().stream()
                    .flatMap(action -> action.getDescr().stream()
                            .filter(desc -> desc.getLang() == lang && desc.isValid()))
                    .collect(Collectors.toList());
            case "R" -> volunteer.getPreferences().getR().stream()
                    .flatMap(action -> action.getDescr().stream()
                            .filter(desc -> desc.getLang() == lang && desc.isValid()))
                    .collect(Collectors.toList());
            default -> volunteer.getPreferences().getU().stream()
                    .flatMap(action -> action.getDescr().stream()
                            .filter(desc -> desc.getLang() == lang && desc.isValid()))
                    .collect(Collectors.toList());
        };
    }


    @Override
    public Errors isError() {
        return Errors.SUCCESS;
    }

    @Transactional
    public Errors updateAction(Long actionId, ActionRequest actionRequest) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();

            // Ustaw dane podstawowe dla akcji
            action.setBegin(actionRequest.getBegin());
            action.setEnd(actionRequest.getEnd());
            action.setLeaderId(actionRequest.getLeaderId());

            // Przekształć istniejące opisy na mapę (lang jako klucz)
            Map<Lang, Description> existingDescriptionsMap = action.getDescr().stream()
                    .collect(Collectors.toMap(Description::getLang, desc -> desc));

            // Przechodzenie przez opisy z żądania i aktualizacja istniejących lub dodawanie nowych
            for (DescriptionRequest descReq : actionRequest.getDescr()) {
                Description description;
                if (existingDescriptionsMap.containsKey(descReq.getLang())) {
                    // Aktualizacja istniejącego opisu
                    description = existingDescriptionsMap.get(descReq.getLang());
                } else {
                    // Dodanie nowego opisu
                    description = new Description();
                    description.setAction(action);
                    action.getDescr().add(description);  // Dodajemy nowy opis do istniejącej kolekcji
                }

                // Aktualizacja danych opisu
                description.setBegin(descReq.getBegin());
                description.setEnd(descReq.getEnd());
                description.setLang(descReq.getLang());
                description.setValid(descReq.isValid());
                description.setFullName(descReq.getFullName());
                description.setShortName(descReq.getShortName());
                description.setPlace(descReq.getPlace());
                description.setAddress(descReq.getAddress());
                description.setDescription(descReq.getDescription());
                description.setHours(descReq.getHours());

                // Aktualizacja ról
                updateRoles(description, descReq.getRoles());
            }

            // Usunięcie opisów, które nie zostały przesłane w żądaniu
            Set<Lang> requestedLangs = actionRequest.getDescr().stream()
                    .map(DescriptionRequest::getLang)
                    .collect(Collectors.toSet());

            action.getDescr().removeIf(desc -> !requestedLangs.contains(desc.getLang()));

            // Zapisanie zmian w repozytorium
            actionRepository.save(action);

            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }


    private void updateRoles(Description description, List<RoleRequest> roleRequests) {
        Map<String, Role> existingRolesMap = description.getRoles().stream()
                .collect(Collectors.toMap(Role::getName, role -> role));

        List<Role> updatedRoles = new ArrayList<>();

        for (RoleRequest roleReq : roleRequests) {
            Role role;
            if (existingRolesMap.containsKey(roleReq.getName())) {
                // Aktualizacja istniejącej roli
                role = existingRolesMap.get(roleReq.getName());
            } else {
                // Dodanie nowej roli
                role = new Role();
                role.setDescription(description); // Ustawienie relacji z opisem
            }

            // Aktualizacja danych roli
            role.setName(roleReq.getName());
            role.setDuties(roleReq.getDuties());

            // Dodanie roli do zaktualizowanej listy
            updatedRoles.add(role);
        }

        // Ustawienie zaktualizowanej listy ról w opisie
        description.setRoles(updatedRoles);
    }




    private void updateExistingDescription(Description description, DescriptionRequest descReq) {
        description.setBegin(descReq.getBegin());
        description.setEnd(descReq.getEnd());
        description.setLang(descReq.getLang());
        description.setValid(descReq.isValid());
        description.setFullName(descReq.getFullName());
        description.setShortName(descReq.getShortName());
        description.setPlace(descReq.getPlace());
        description.setAddress(descReq.getAddress());
        description.setDescription(descReq.getDescription());
        description.setHours(descReq.getHours());

        // Zaktualizuj role
        description.getRoles().clear();
        List<Role> updatedRoles = descReq.getRoles().stream()
                .map(this::createRoleFromRequest)
                .toList();
        updatedRoles.forEach(role -> role.setDescription(description));
        description.getRoles().addAll(updatedRoles);
    }


    private Description createDescriptionFromRequest(DescriptionRequest descReq, Action action) {
        Description description = new Description();
        description.setBegin(descReq.getBegin());
        description.setEnd(descReq.getEnd());
        description.setLang(descReq.getLang());

        // Ustaw dane wersji dziedziczone przez Description
        description.setValid(descReq.isValid());
        description.setFullName(descReq.getFullName());
        description.setShortName(descReq.getShortName());
        description.setPlace(descReq.getPlace());
        description.setAddress(descReq.getAddress());
        description.setDescription(descReq.getDescription());
        description.setHours(descReq.getHours());

        // Przetwórz role i przypisz je do Description
        List<Role> roles = descReq.getRoles().stream()
                .map(this::createRoleFromRequest)
                .toList();

        // Ustaw relację między Description a Role
        roles.forEach(role -> role.setDescription(description));
        description.setRoles(roles);

        // Przypisz akcję do Description
        description.setAction(action);

        return description;
    }





    private Role createRoleFromRequest(RoleRequest roleReq) {
        Role role = new Role();
        role.setName(roleReq.getName());
        role.setDuties(roleReq.getDuties());
        return role;
    }


    public Action getAction(Long actionId) {
        return actionRepository.findById(actionId).get();
    }

    public Action getActionTmp(Long actionId) {
        Action action = actionRepository.findById(actionId).get();

//        action.getDescr().get().g
        return action;
    }

    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }


    public List<String> getAllHeadings(Lang language) {
        return actionRepository.findAll().stream()
                .map(action -> getDesc(action.getActionId(), language).getFullName())
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }


}
