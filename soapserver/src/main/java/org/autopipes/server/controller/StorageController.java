package org.autopipes.server.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.autopipes.model.AreaCutSheet;
import org.autopipes.model.AreaCutSheet.BranchInfo;
import org.autopipes.model.AreaCutSheet.CutSheetInfo;
import org.autopipes.model.AreaCutSheet.MainCutSheetInfo;
import org.autopipes.model.DrawingArea;
import org.autopipes.model.FloorDrawing;
import org.autopipes.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorageController {
	private static Logger logger = Logger.getLogger(StorageController.class);

	@Autowired
    @Qualifier("JpaStorageService")
    private StorageService service;
	
	@GetMapping(value="/service/drawing", produces=MediaType.APPLICATION_JSON_VALUE)
	List<FloorDrawing> findAllDrawings(){
		return service.findAllDrawings();
	}
	
	@GetMapping(value="/service/drawing/{dwgId}/area", produces=MediaType.APPLICATION_JSON_VALUE)
 	List<DrawingArea> findDrawingAreas(@PathVariable("dwgId") Long dwgId){
 		return service.findDrawingAreas(dwgId);
 	}
	@GetMapping(value="/service/drawing/{dwgId}/area/{areaId}", produces=MediaType.APPLICATION_JSON_VALUE)
	DrawingArea findOneDrawingArea(@PathVariable("dwgId") final Long dwgId,
			@PathVariable("areaId") final Long areaId,
			@RequestParam(value = "body", required = false) boolean withBody,
			@RequestParam(value = "cutsheet", required = false) boolean withCutSheet) {
		return service.findOneDrawingArea(dwgId, areaId, withBody, withCutSheet);
	}
	
	private AreaCutSheet getCutSheet(final long dwgId, final long areaId) {
	    try{
	    	FloorDrawing dwg = service.findOneDrawing(dwgId);
	        if(dwg == null){
	        	throw new IllegalArgumentException(
	        			"Unknown drawing id " + dwgId);
	        }
	        DrawingArea area = service.findOneDrawingArea(dwgId, areaId, false, true);
	        if(area == null){
	        	throw new IllegalArgumentException(
	        			"Unknown area id " + areaId + " for drawing " + dwg.getDwgName());
	        }
	    	return area.getAreaCutSheet();
	    }catch(Exception e){
	    	logger.error("getCutSheet", e);
	    	return null;
	    }
	}
	
	@GetMapping(value="/service/drawing/{dwgId}/area/{areaId}/branch", produces=MediaType.APPLICATION_JSON_VALUE)
	public Collection<BranchInfo> getBranchInfoForArea(@PathVariable("dwgId") final long dwgId,
			@PathVariable("areaId") final long areaId){
		AreaCutSheet cutSheet = getCutSheet(dwgId, areaId);
		Collection<BranchInfo> ret = new ArrayList<BranchInfo>();
		if(cutSheet != null) {
			ret = cutSheet.getBranchMap().values();
		}
		return ret;
    }
	
	@GetMapping(value="/service/drawing/{dwgId}/area/{areaId}/main/threaded", produces=MediaType.APPLICATION_JSON_VALUE)
    public List<CutSheetInfo> getMainThreadedList(@PathVariable("dwgId") final long dwgId, 
    		@PathVariable("areaId") final long areaId){
        AreaCutSheet cutSheet = getCutSheet(dwgId, areaId);
        List<CutSheetInfo> ret = new ArrayList<CutSheetInfo>();
        if(cutSheet != null) {
        	ret = cutSheet.getMainThreadedList();
        }
        return ret;
    }
    
	@GetMapping(value="/service/drawing/{dwgId}/area/{areaId}/main/grooved", produces=MediaType.APPLICATION_JSON_VALUE)
    public List<MainCutSheetInfo> getMainGroovedList(@PathVariable("dwgId") final long dwgId,
    		@PathVariable("areaId") final long areaId){
    	AreaCutSheet cutSheet = getCutSheet(dwgId, areaId);
    	List<MainCutSheetInfo> ret = new ArrayList<MainCutSheetInfo>();
    	if(cutSheet != null) {
    		ret = cutSheet.getMainGroovedList();
    	}
        return ret;
    }
	
	@GetMapping(value="/service/drawing/{dwgId}/area/{areaId}/main/welded", produces=MediaType.APPLICATION_JSON_VALUE)
    public List<MainCutSheetInfo> getMainWeldedList(@PathVariable("dwgId") final long dwgId,
    		@PathVariable("areaId") final long areaId){
    	AreaCutSheet cutSheet = getCutSheet(dwgId, areaId);
    	List<MainCutSheetInfo> ret = new ArrayList<MainCutSheetInfo>();
    	if(cutSheet != null) {
    		ret = cutSheet.getMainWeldedList();
    	}
        return ret;
    }

	
	@DeleteMapping("/service/drawing/{dwgId}/area/{areaId}")
	void deleteArea(@PathVariable("dwgId") final Long dwgId,
			@PathVariable("areaId") final Long areaId) {
		service.deleteArea(dwgId, areaId);
	}
	
}
