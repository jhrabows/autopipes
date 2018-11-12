package org.autopipes.service;

import java.util.List;
import java.util.Optional;

import org.autopipes.model.DrawingArea;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DrawingAreaRepository extends CrudRepository<DrawingArea, DrawingArea.Key> {
	List<DrawingArea> findByKey_DrawingId(long dwgId);
	
	Optional<DrawingArea> findByKey_DrawingIdAndAreaName(long dwgId, String areaName);
	
	@Query("SELECT max(da.key.areaId) FROM DrawingArea da WHERE da.key.drawingId = :dwgId")
	Long getMaxAreaId(@Param("dwgId") long dwgId); 
}
