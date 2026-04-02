package biz.codex55.web_service.controller;

import biz.codex55.web_service.dto.GradeResponse;
import biz.codex55.web_service.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/school-grade")
@RequiredArgsConstructor
public class SchoolGradeController {

    private final GradeRepository gradeRepository;

    @GetMapping
    public List<GradeResponse> getAllGrades() {
        return gradeRepository.findAll()
                .stream()
                .map(g -> new GradeResponse(g.getId(), g.getName()))
                .toList();
    }
}
