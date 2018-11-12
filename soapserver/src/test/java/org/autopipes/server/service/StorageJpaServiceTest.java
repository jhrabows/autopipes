package org.autopipes.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.autopipes.model.DrawingArea;
import org.autopipes.model.FloorDrawing;
import org.autopipes.service.DrawingAreaRepository;
import org.autopipes.service.DrawingRepository;
import org.autopipes.service.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StorageJpaServiceTest {
	private static Logger LOG = LoggerFactory.getLogger(StorageJpaServiceTest.class);
	
    @Autowired
	private DrawingRepository drawingRepository;
    @Autowired
	private DrawingAreaRepository drawingAreaRepository;
    
    @Autowired
    private StorageService service;
    
    @Value("classpath:test-dwg-1.xml")
    private Resource dwg1Res;
    @Value("classpath:test-cfg-1.xml")
    private Resource cfg1Res;

	@Autowired
	private Jaxb2Marshaller jaxb2Marshaller;
	
	private Object unmarshalResource(Resource res) throws IOException {
		InputStream is = res.getInputStream();
    	Reader reader = new InputStreamReader(is);
    	Source source = new StreamSource(reader);
    	return jaxb2Marshaller.unmarshal(source);
	}
	
    @Test
	public void testDrawingAreaCRUD() {
    	assertNotNull(drawingAreaRepository);
    	assertNotNull(service);
    	List<DrawingArea> areas = drawingAreaRepository.findByKey_DrawingId(-1L);
    	assertEquals(0, areas.size());
    	Long missingId = service.maxAreaId(-1L);
    	assertNull(missingId);
	}
    
    @Test
    public void testDrawingCRUD() {
    	assertNotNull(drawingRepository);
    	assertNotNull(service);
    	Iterable<FloorDrawing> drawings = drawingRepository.findAll();
    	List<FloorDrawing>	jpadrawings = service.findAllDrawings();
    	assertNotNull(drawings);
    	assertNotNull(jpadrawings);
    	assertNotNull(jpadrawings);
    	FloorDrawing missingDwg = service.findOneDrawing(-1L);
    	assertNull(missingDwg);
    	Long missingId = service.findDrawingId("__MISSING__");
    	assertNull(missingId);
    }
    
    @Test
    public void testInsert() throws Exception {
    	assertNotNull(dwg1Res);
    	FloorDrawing cfg1 = (FloorDrawing) unmarshalResource(cfg1Res);
    	service.mergeDrawing(cfg1);
    	Long id = cfg1.getId();
    	FloorDrawing cfg1Dwg = service.findOneDrawing(id);
        jaxb2Marshaller.marshal(cfg1Dwg, new StreamResult(System.out));
        DrawingArea dwg1 = (DrawingArea) unmarshalResource(dwg1Res);
        service.mergeArea(dwg1);
        List<DrawingArea> areas = service.findDrawingAreas(id);
        assertTrue(areas.size() > 0);
     }

}
