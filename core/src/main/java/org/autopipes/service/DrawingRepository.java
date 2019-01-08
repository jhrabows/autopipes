package org.autopipes.service;

import java.util.Optional;

import org.autopipes.model.FloorDrawing;
import org.springframework.data.repository.CrudRepository;

public interface DrawingRepository extends CrudRepository<FloorDrawing, Long> {
	Optional<FloorDrawing> findByDwgName(String name);
}
