package org.ideoholic.timetable.service;

import java.util.List;

import org.ideoholic.timetable.entity.ClassMaster;
import org.ideoholic.timetable.repository.ClassMasterRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassMasterService {

    private final ClassMasterRepository repository;

    public ClassMaster save(
            ClassMaster classMaster) {

        return repository.save(classMaster);
    }

    public List<ClassMaster> findAll() {

        return repository.findAll();
    }
}