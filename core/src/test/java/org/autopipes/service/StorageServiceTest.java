package org.autopipes.service;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.autopipes.model.DrawingArea;
import org.autopipes.model.FloorDrawing;
import org.autopipes.model.AreaCutSheet.CutSheetInfo;
import org.autopipes.model.DrawingArea.Readiness;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
//import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.context.ContextConfiguration;

// specify the BeanConfigurationFiles to use for auto-wiring the properties of this class
@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:test-app-context.xml" /*, "classpath:test-pipe-context.xml" */})
public class StorageServiceTest  {
    private static Logger logger = Logger.getLogger(StorageServiceTest.class);
    @Autowired
	private Marshaller jaxb2Marshaller;
    @Autowired
	private StorageService storageService;
    @Autowired
	private ReportingService reportingService;
    @Autowired
	private FloorDrawing cfgTest1;
    @Autowired
    private DrawingArea dwgTest1;

    @Test
	public void testSpringSetup()throws Exception{
        Result res = new StreamResult(System.out);
        Assert.assertNotNull(cfgTest1);
        jaxb2Marshaller.marshal(cfgTest1, res);
        Assert.assertNotNull(dwgTest1);
        jaxb2Marshaller.marshal(dwgTest1, res);
	}
    
    @Test
	public void testQuery(){
		List<FloorDrawing> drawings = storageService.findAllDrawings();
		logger.info("Got " + drawings.size() + " drawings");
		for(FloorDrawing dwg : drawings){
			List<DrawingArea> areas = storageService.findDrawingAreas(dwg.getId());
			logger.info("Drawing: " + dwg.getDwgName() + " has " + areas.size() + " areas.");
			for(DrawingArea a : areas){
				logger.info("Found area: " +a.getAreaName()
						+ ",ready=" + a.getAreaReadiness() + "(" + a.getDefectCount() + ")");
			}

		}
	}
    
    @Test
	public void testQueryDrawing() throws Exception{
		List<FloorDrawing> drawings = storageService.findAllDrawings();
		logger.info("Got " + drawings.size() + " drawings");
		for(FloorDrawing dwg : drawings){
			FloorDrawing dwgDetail = storageService.findOneDrawing(dwg.getId());
			logger.info("Drawing: " + dwg.getDwgName());
	        jaxb2Marshaller.marshal(dwgDetail, new StreamResult(System.out));
		}
	}

    @Test
	public void testQueryArea(){
		Long id = storageService.findDrawingId(cfgTest1.getDwgName());
		List<DrawingArea> areas = storageService.findDrawingAreas(id);
		logger.info("Got " + areas.size() + " areas for drawing " + id);
	}
    @Test
	public void testThreadedMainReport(){
		
		Long id = storageService.findDrawingId("Test-1-before");
		List<DrawingArea> areas = storageService.findDrawingAreas(id);
		logger.info("Got " + areas.size() + " areas for drawing " + id);
		if(areas.size() > 0){
			DrawingArea area = areas.get(0);
			DrawingArea areaDetail =
			storageService.findOneDrawingArea(id, area.getAreaId(), false, true);
			List<CutSheetInfo> rpt = areaDetail.getAreaCutSheet().getMainThreadedList();
			logger.info("Got " + rpt.size() + " pipes for area " + area.getAreaName());
		//	Collections.sort(rpt, new MainCutSheetComparator());
			this.reportingService.sortThreadedMain(rpt);
			for(CutSheetInfo csi : rpt){
				logger.info("M-" + csi.getPipe().getId());
			}
		}
	}
/*	
    public static class MainCutSheetComparator implements Comparator<CutSheetInfo>{
		@Override
		public int compare(CutSheetInfo c1, CutSheetInfo c2) {
			return c1.getPipe().getId().compareTo(c2.getPipe().getId());
		}
    }
*/
    @Test
	public void testQueryOneArea() throws XmlMappingException, IOException{
		Long id = storageService.findDrawingId(cfgTest1.getDwgName());
		List<DrawingArea> areas = storageService.findDrawingAreas(id);
		logger.info("Got " + areas.size() + " areas for drawing " + id);
		if(areas.size() > 0){
			DrawingArea area = areas.get(0);
			DrawingArea areaDetail =
			storageService.findOneDrawingArea(id, area.getAreaId(), true, true);
	        jaxb2Marshaller.marshal(areaDetail, new StreamResult(System.out));
	        areaDetail = storageService.findOneDrawingArea(id, area.getAreaId(), true, false);
	        jaxb2Marshaller.marshal(areaDetail, new StreamResult(System.out));
		}
	}
    
    @Test
	public void testInsertCfg() throws Exception{
		Long id = storageService.findDrawingId(cfgTest1.getDwgName());
		if(id != null){
		   storageService.deleteDrawing(id);
		}
		testUpdateCfg();
	}
    @Test
	public void testInsertArea() throws Exception{
		Long drawingId = storageService.findDrawingId(cfgTest1.getDwgName());
		Assert.assertNotNull(drawingId);
		dwgTest1.setDrawingId(drawingId);
		Long areaId = storageService.findAreaId(drawingId, dwgTest1.getAreaName());
		if(areaId != null){
		   storageService.deleteArea(drawingId, areaId);
		}
		areaId = storageService.maxAreaId(drawingId);
		dwgTest1.setAreaId(areaId.longValue() + 1);
		dwgTest1.setAreaReadiness(Readiness.Ready);
		logger.info("Inserting: dwg=" + drawingId);
		storageService.mergeArea(dwgTest1);
	}
    @Test
	public void testUpdateCfg() throws Exception{
		storageService.mergeDrawing(cfgTest1);
	}
    @Test
	public void testUpdateArea() throws Exception{
		Long drawingId = storageService.findDrawingId(cfgTest1.getDwgName());
		Assert.assertNotNull(drawingId);
		dwgTest1.setDrawingId(drawingId);
		Long areaId = storageService.findAreaId(drawingId, dwgTest1.getAreaName());
		Assert.assertNotNull(areaId);
		dwgTest1.setAreaId(areaId);
        storageService.mergeArea(dwgTest1);
        logger.info("Updating dwg=" + drawingId);
	}

	// getters and setters
	public Marshaller getJaxb2Marshaller() {
		return jaxb2Marshaller;
	}
	public void setJaxb2Marshaller(final Marshaller marshaller) {
		this.jaxb2Marshaller = marshaller;
	}
	public StorageService getStorageService() {
		return storageService;
	}
	public void setStorageService(final StorageService storageService) {
		this.storageService = storageService;
	}
	public ReportingService getReportingService() {
		return reportingService;
	}

	public void setReportingService(final ReportingService reportingService) {
		this.reportingService = reportingService;
	}
	public FloorDrawing getCfgTest1() {
		return cfgTest1;
	}
	public void setCfgTest1(final FloorDrawing cfgTest1) {
		this.cfgTest1 = cfgTest1;
	}
	public DrawingArea getDwgTest1() {
		return dwgTest1;
	}
	public void setDwgTest1(final DrawingArea dwgTest1) {
		this.dwgTest1 = dwgTest1;
	}

}
