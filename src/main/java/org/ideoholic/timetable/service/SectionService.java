package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.entity.Section;
import org.ideoholic.timetable.repository.SectionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository repository;

    public Section save(Section section) {
        return repository.save(section);
    }

    public List<Section> findAll() {
        return repository.findAll();
    }
}