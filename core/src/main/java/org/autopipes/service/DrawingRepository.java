package org.autopipes.service;

import org.autopipes.model.FloorDrawing;
import org.springframework.data.repository.CrudRepository;

public interface DrawingRepository extends CrudRepository<FloorDrawing, Long> {
	FloorDrawing findByDwgName(String name);
}
