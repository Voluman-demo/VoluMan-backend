//package com.example.demo.Volunteer.Candidate;
//
//
//import com.example.demo.Log.EventType;
//import com.example.demo.Log.LogService;
//import com.example.demo.Model.ID;
//import com.example.demo.Volunteer.Position.Position;
//import com.example.demo.Volunteer.Volunteer;
//import com.example.demo.Volunteer.VolunteerRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/candidates")
//public class CandidateController {
//
//    private final CandidateRepository candidateRepository;
//    private final CandidateService candidateService;
//    private final VolunteerRepository volunteerRepository;
//    private final LogService logService;
//
//    public CandidateController(CandidateRepository candidateRepository, CandidateService candidateService, VolunteerRepository volunteerRepository, LogService logService) {
//        this.candidateRepository = candidateRepository;
//        this.candidateService = candidateService;
//        this.volunteerRepository = volunteerRepository;
//        this.logService = logService;
//    }
//
//    @GetMapping("")
//    public ResponseEntity<List<Candidate>> getCandidates(@RequestParam ID recruiterId) {
//        if (volunteerRepository.existsByVolunteerIdAndPosition(recruiterId, Position.RECRUITER)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        List<Candidate> candidates = candidateRepository.findAll();
//        if (candidates.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        return ResponseEntity.ok(candidates);
//    }
//
//
//    @GetMapping("/{idCandidate}")
//    public ResponseEntity<Candidate> getCandidate(@PathVariable ID idCandidate, @RequestParam ID recruiterId) { //DONE
//        if (volunteerRepository.existsByVolunteerIdAndPosition(recruiterId, Position.RECRUITER)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
//        return candidate.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//
//    @PostMapping("")
//    public ResponseEntity<Volunteer> addCandidate(@RequestBody Volunteer candidate) {
//
//        Volunteer savedCandidate = candidateService.addCandidate(candidate);
//
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
//    }
//
//    @PostMapping("{idCandidate}/accept")
//    public ResponseEntity<Volunteer> acceptCandidate(@PathVariable ID idCandidate, @RequestParam ID recruiterId) {
//        Optional<Volunteer> candidate = volunteerRepository.findById(idCandidate);
//        if (candidate.isPresent()) {
//            candidateService.acceptCandidate(idCandidate, recruiterId);
//
//            logService.logCandidate(candidate.get(), EventType.ACCEPT, "Candidate accepted by recruiter " + recruiterId);
//
//            return ResponseEntity.ok(candidate.get());
//        }
//
//        return ResponseEntity.notFound().build();
//    }
//
//    @DeleteMapping("{idCandidate}/refuse")
//    public ResponseEntity<Volunteer> refuseCandidate(@PathVariable ID idCandidate, @RequestParam ID recruiterId) {
//        if (volunteerRepository.existsByVolunteerIdAndPosition(recruiterId, Position.RECRUITER)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        Optional<Object> candidate = volunteerRepository.findByVolunteerIdAndPosition(idCandidate, Position.CANDIDATE);
//        if (candidate.isPresent()) {
//            candidateService.refuseCandidate(idCandidate, recruiterId);
//
//            logService.logCandidate((Volunteer) candidate.get(), EventType.REFUSE, "Candidate refused by recruiter " + recruiterId);
//
//            return ResponseEntity.ok((Volunteer) candidate.get());
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//}
