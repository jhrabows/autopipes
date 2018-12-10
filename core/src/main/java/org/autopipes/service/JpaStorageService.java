package org.autopipes.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
//import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.autopipes.model.AreaBody;
import org.autopipes.model.AreaCutSheet;
import org.autopipes.model.AreaOptions;
import org.autopipes.model.DrawingArea;
import org.autopipes.model.DrawingArea.Key;
import org.autopipes.model.DrawingOptions;
import org.autopipes.model.FloorDrawing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("JpaStorageService")
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
public class JpaStorageService implements StorageService {
	
	@Autowired
	private Jaxb2Marshaller jaxb2Marshaller;
	
//	@PersistenceContext
//    EntityManager em;
	
    @Autowired
	private DrawingRepository drawingRepository;
    @Autowired
	private DrawingAreaRepository drawingAreaRepository;
	
	public JpaStorageService(){
	}
	
	private String marshalObject(Object obj) {
		String ret = null;
		if(obj != null) {
		    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    jaxb2Marshaller.marshal(obj, new StreamResult(baos));
			ret = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		}
		return ret;
	}
	
	private Object unmarshalString(String xml) {
		Object ret = null;
		if(xml != null && !xml.isEmpty()) {
	        	Reader reader = new StringReader(xml);
		    	Source source = new StreamSource(reader);
	        	try {
					ret = jaxb2Marshaller.unmarshal(source);
				} catch (Exception e) {
					e.printStackTrace();
				}	
		}
        return ret;
	}
	
	private void updateDrawingFromStorage(FloorDrawing dwg) {
		if(dwg != null) {
	    	String orx = dwg.getOptionsRootXml();
	    	DrawingOptions optionsRoot = (DrawingOptions)unmarshalString(orx);
			dwg.setOptionsRoot(optionsRoot);
		}
	}
	
	private void updateDrawingAreaFromStorage(DrawingArea dwgArea, boolean withBody, boolean withCutSheet) {
		if(dwgArea != null) {
			if(withBody) {
		    	String abx = dwgArea.getAreaBodyXml();
		    	AreaBody body = (AreaBody)unmarshalString(abx);
		    	dwgArea.setAreaBody(body);
			}
			if(withCutSheet) {
		    	String acx = dwgArea.getAreaCutSheetXml();
		    	AreaCutSheet areaCutSheet = (AreaCutSheet)unmarshalString(acx);
		    	if(areaCutSheet != null) {
		    		areaCutSheet.postDeserialize();
		    	}
		    	dwgArea.setAreaCutSheet(areaCutSheet);
			}
		}
	}
	
	private void updateDrawingForStorage(FloorDrawing dwg) {
		if(dwg != null) {
			DrawingOptions options = dwg.getOptionsRoot();
			if(options != null){
				  options.preSerialize();
				  String optionsXml = marshalObject(options);
				  dwg.setOptionsRootXml(optionsXml);
			}
		}
	}
	
	private void updateDrawingAreaForStorage(DrawingArea area) {
		if(area != null) {
			AreaCutSheet acs = area.getAreaCutSheet();
			if(acs != null){
				acs.preSerialize();
				  String xml = marshalObject(acs);
				  area.setAreaCutSheetXml(xml);
			}
			AreaBody ab = area.getAreaBody();
			if(ab != null) {
				  String xml = marshalObject(ab);
				  area.setAreaBodyXml(xml);
			}
			AreaOptions ao = area.getAreaOptions();
			if(ao != null) {
				  String xml = marshalObject(ao);
				  area.setAreaOptionsXml(xml);
			}
		}
	}
	
// API
	public List<FloorDrawing> findAllDrawings() {
		List<FloorDrawing> ret = new ArrayList<FloorDrawing>();
		Iterable<FloorDrawing> list = drawingRepository.findAll();
        for(FloorDrawing dwg : list) {
        	updateDrawingFromStorage(dwg);
			ret.add(dwg);
        }
        return ret;
	}

	public FloorDrawing findOneDrawing(Long id) {
		FloorDrawing dwg = drawingRepository.findById(id).orElse(null);
		updateDrawingFromStorage(dwg);
		return dwg;
	}

	public Long findDrawingId(String name) {
		Long ret = null;
		FloorDrawing dwg = drawingRepository.findByDwgName(name);
		if(dwg != null) {
			ret = dwg.getId();
		}
		return ret;
	}

	@Transactional
	public void mergeDrawing(FloorDrawing dwg) throws Exception {
		Long id = dwg.getId();
		if(id != null) {
			// check if that id is still present
			FloorDrawing testDwg = drawingRepository.findById(id).orElse(null);
			if(testDwg == null) {
				id = null;
			}
		}
		if(id == null) {
			id = findDrawingId(dwg.getDwgName());
		}
		dwg.setId(id);
		updateDrawingForStorage(dwg);
		FloorDrawing savedDwg = drawingRepository.save(dwg);
		if(id== null){
			dwg.setId(savedDwg.getId());
		}
	}

	@Transactional
	public void deleteDrawing(Long id) {
		drawingRepository.deleteById(id);
	}

	@Transactional
	public DrawingArea mergeArea(DrawingArea area) throws Exception {
		Long dwgId = area.getDrawingId();
		if(dwgId == null){
		  throw new IllegalArgumentException("Missing drawing id");
		}
		Long areaId = area.getAreaId();
		DrawingArea testArea = null;
		if(areaId != null) {
		  testArea = drawingAreaRepository.findById(new Key(dwgId, areaId)).orElse(null);
		// check if that id is still present
		  if(testArea == null) {
			  areaId = null;
		  }
		}
		if(areaId == null) {
		  Long maxId = drawingAreaRepository.getMaxAreaId(dwgId);
		  areaId = (maxId == null ? 0L : maxId.longValue()) + 1;
		}
		if(area.getAreaName() == null){
			// it is not guaranteed that this name is not present though
			  area.setAreaName("Area-" + areaId);
		}
		area.setAreaId(areaId);
		if(testArea != null) {
			// preserve missing attributes
			if(area.getAreaReadiness() == null) {
				area.setAreaReadiness(testArea.getAreaReadiness());
				area.setDefectCount(testArea.getDefectCount());
			}
			if(area.getAreaBody() == null) {
				area.setAreaBodyXml(testArea.getAreaBodyXml());
			}
			if(area.getAreaOptions() == null) {
				area.setAreaOptionsXml(testArea.getAreaOptionsXml());
			}
			if(area.getAreaCutSheet() == null) {
				area.setAreaCutSheetXml(testArea.getAreaCutSheetXml());
			}
		}
		updateDrawingAreaForStorage(area);
		drawingAreaRepository.save(area);
		return area;
	}

	public List<DrawingArea> findDrawingAreas(Long dwgId) {
		return drawingAreaRepository.findByKey_DrawingId(dwgId);
	}

	public DrawingArea findOneDrawingArea(Long dwgId, Long areaId, boolean withBody, boolean withCutSheet) {
		DrawingArea ret = drawingAreaRepository.findById(new Key(dwgId, areaId)).orElse(null);
		if(ret != null) {
			updateDrawingAreaFromStorage(ret, withBody, withCutSheet);
		}
		return ret;
	}

	public Long findAreaId(Long drawingId, String areaName) {
		DrawingArea ret = drawingAreaRepository.findByKey_DrawingIdAndAreaName(drawingId, areaName).orElse(null);
		return ret == null ? null : ret.getAreaId();
	}

	@Transactional
	public void deleteArea(Long drawingId, Long areaId) {
		drawingAreaRepository.deleteById(new Key(drawingId, areaId));
	}

	public Long maxAreaId(Long dwgId) {
		return drawingAreaRepository.getMaxAreaId(dwgId);
	}

}
