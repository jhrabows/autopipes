package org.autopipes.dwg.spring;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.autopipes.model.AreaCutSheet.BranchInfo;
import org.autopipes.model.AreaCutSheet.MainCutSheetInfo;
import org.autopipes.model.DrawingArea;
import org.autopipes.model.FloorDrawing;
import org.autopipes.service.AnalyzerService;
import org.autopipes.service.ReportingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-context-junit4.xml" , "classpath:spring-pipe.xml" })
public final class SpringTest {
	@Test
	public void dummyTest() {
		
	}
/*	
	// injected services
	@Autowired
	@Qualifier("reportingService")
	private ReportingService reportingService;
	@Autowired
	@Qualifier("analyzerService")
	private AnalyzerService analyzerService;
	@Autowired
	@Qualifier("jaxb2Marshaller")
	private Marshaller jaxb2Marshaller;
	
	// injected xml test files
	@Autowired
	@Qualifier("testWeldSameCfg")
	private FloorDrawing testWeldSameCfg;
	@Autowired
	@Qualifier("testWeldSame")
    private DrawingArea testWeldSame;

	@Autowired
	@Qualifier("testWeldCrossCfg")
	private FloorDrawing testWeldCrossCfg;
	@Autowired
	@Qualifier("testWeldCross")
    private DrawingArea testWeldCross;
	
	@Autowired
	@Qualifier("testWeldCross2Cfg")
	private FloorDrawing testWeldCross2Cfg;
	@Autowired
	@Qualifier("testWeldCross2")
    private DrawingArea testWeldCross2;

		
	@Autowired
	@Qualifier("testDwgCfg")
	private FloorDrawing testDwgCfg;
	@Autowired
	@Qualifier("testDwgA1")
    private DrawingArea testDwgA1;

	@Autowired
	@Qualifier("errDwgCfg")
	private FloorDrawing errDwgCfg;
	@Autowired
	@Qualifier("errDwgA1")
    private DrawingArea errDwgA1;
	
	
	@Autowired
	@Qualifier("thr2Cfg")
	private FloorDrawing thr2Cfg;
	@Autowired
	@Qualifier("thr2Dwg")
    private DrawingArea thr2Dwg;
	
	@Autowired
	@Qualifier("weld1Cfg")
	private FloorDrawing weld1Cfg;
	@Autowired
	@Qualifier("weld1Dwg")
    private DrawingArea weld1Dwg;
	@Autowired
	@Qualifier("weld2Dwg")
    private DrawingArea weld2Dwg;
	@Autowired
	@Qualifier("weld3Dwg")
    private DrawingArea weld3Dwg;
	@Autowired
	@Qualifier("weld4Dwg")
    private DrawingArea weld4Dwg;
	@Autowired
	@Qualifier("weld5Dwg")
    private DrawingArea weld5Dwg;
	@Autowired
	@Qualifier("weld6Dwg")
    private DrawingArea weld6Dwg;

	@Autowired
	@Qualifier("weldGrCfg")
	private FloorDrawing weldGrCfg;
	@Autowired
	@Qualifier("groovedCfg")
	private FloorDrawing groovedCfg;
	@Autowired
	@Qualifier("weldGr1Dwg")
    private DrawingArea weldGr1Dwg;
	@Autowired
	@Qualifier("weldGr2Dwg")
    private DrawingArea weldGr2Dwg;

	@Autowired
	@Qualifier("weld7Cfg")
	private FloorDrawing weld7Cfg;
	@Autowired
	@Qualifier("weld7Dwg")
    private DrawingArea weld7Dwg;
	
	@Autowired
	@Qualifier("cfgTest200")
	private FloorDrawing cfgTest200;
	@Autowired
	@Qualifier("dwgTest200")
    private DrawingArea dwgTest200;
	
	@Autowired
	@Qualifier("cell3Cfg")
	private FloorDrawing cell3Cfg;
	@Autowired
	@Qualifier("cell3Dwg")
    private DrawingArea cell3Dwg;
	

	
	@Test
	public void testInitialLoad() throws XmlMappingException, IOException{
    	analyzerService.validateArea(testDwgCfg, testDwgA1);
    	testDwgA1.setAreaId(1L);
    	reportingService.renderAreaStatus(testDwgCfg, testDwgA1);
   		reportingService.renderAreaIdStamp(testDwgCfg, testDwgA1);
   		testDwgA1.setAreaBody(null);
		jaxb2Marshaller.marshal(testDwgA1, new StreamResult(System.out));
	}
	
	@Test
	public void testThr2() throws XmlMappingException, IOException{
    	analyzerService.validateArea(thr2Cfg, thr2Dwg);
    	analyzerService.buildCutSheetReport(thr2Cfg, thr2Dwg);

    	reportingService.renderMainLabels(thr2Cfg, thr2Dwg);
        reportingService.renderSizes(thr2Cfg, thr2Dwg, true, true, true, true);
        thr2Dwg.setAreaBody(null);
		jaxb2Marshaller.marshal(thr2Dwg, new StreamResult(System.out));
	}
	
	@Test
	public void testMainBreak() throws XmlMappingException, IOException{
    	analyzerService.validateArea(errDwgCfg, errDwgA1);
	}
	
	@Test
	public void testWeld1() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weld1Cfg, weld1Dwg);
	}
	
	@Test
	public void testWeld2() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weld1Cfg, weld2Dwg);
	}
	
	@Test
	public void testWeld3() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weld1Cfg, weld3Dwg);
    	analyzerService.buildCutSheetReport(weld1Cfg, weld3Dwg);

    	reportingService.renderMainLabels(weld1Cfg, weld3Dwg);
        reportingService.renderSizes(weld1Cfg, weld3Dwg, true, true, true, true);
        weld3Dwg.setAreaBody(null);
		jaxb2Marshaller.marshal(weld3Dwg, new StreamResult(System.out));
	}
	
	@Test
	public void testWeld4() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weld1Cfg, weld4Dwg);
    	analyzerService.buildCutSheetReport(weld1Cfg, weld4Dwg);

    	reportingService.renderMainLabels(weld1Cfg, weld4Dwg);
        reportingService.renderSizes(weld1Cfg, weld4Dwg, true, true, true, true);
        weld4Dwg.setAreaBody(null);
		jaxb2Marshaller.marshal(weld4Dwg, new StreamResult(System.out));
	}
	
	@Test
	public void testWeld5() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weld1Cfg, weld5Dwg);
	}
	@Test
	public void testWeld6() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weld1Cfg, weld6Dwg);
    	analyzerService.buildCutSheetReport(weld1Cfg, weld6Dwg);
    	reportingService.renderMainLabels(weld1Cfg, weld6Dwg);
        reportingService.renderSizes(weld1Cfg, weld6Dwg, true, true, true, true);
        weld6Dwg.setAreaBody(null);
		jaxb2Marshaller.marshal(weld6Dwg, new StreamResult(System.out));
	}
	
	@Test
	public void testWeld7() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weld7Cfg, weld7Dwg);
    	analyzerService.buildCutSheetReport(weld7Cfg, weld7Dwg);
    	Map<Integer, BranchInfo> map = weld7Dwg.getAreaCutSheet().getBranchMap();
    	reportingService.renderMainLabels(weld7Cfg, weld7Dwg);
        reportingService.renderSizes(weld7Cfg, weld7Dwg, true, true, true, true);
        weld7Dwg.setAreaBody(null);
		jaxb2Marshaller.marshal(weld7Dwg, new StreamResult(System.out));
	}
	
	@Test
	public void testWeldCross() throws XmlMappingException, IOException{
    	analyzerService.validateArea(testWeldCrossCfg, testWeldCross);
    	analyzerService.buildCutSheetReport(testWeldCrossCfg, testWeldCross);
    	List<MainCutSheetInfo> ret = testWeldCross.getAreaCutSheet().getMainWeldedList();
    	
    	jaxb2Marshaller.marshal(testWeldCross, new StreamResult(System.out));
    	
	}
	
	@Test
	public void testWeldCross2() throws XmlMappingException, IOException{
    	analyzerService.validateArea(testWeldCross2Cfg, testWeldCross2);
    	analyzerService.buildCutSheetReport(testWeldCross2Cfg, testWeldCross2);
    	List<MainCutSheetInfo> ret = testWeldCross2.getAreaCutSheet().getMainWeldedList();
    	
    	jaxb2Marshaller.marshal(testWeldCross2, new StreamResult(System.out));
    	
	}


	@Test
	public void testWeldSame() throws XmlMappingException, IOException{
    	analyzerService.validateArea(testWeldSameCfg, testWeldSame);
    	analyzerService.buildCutSheetReport(testWeldSameCfg, testWeldSame);
    	List<MainCutSheetInfo> ret = testWeldSame.getAreaCutSheet().getMainWeldedList();
    	
    	jaxb2Marshaller.marshal(testWeldSame, new StreamResult(System.out));
    	
	}

	
	
	
	@Test
	public void testWeldGr1() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weldGrCfg, weldGr1Dwg);
    	analyzerService.buildCutSheetReport(weldGrCfg, weldGr1Dwg);
    	Map<Integer, BranchInfo> map = weld7Dwg.getAreaCutSheet().getBranchMap();
    	int x=0;
	}
	
	
	@Test
	public void testGrooved1() throws XmlMappingException, IOException{
    	analyzerService.validateArea(groovedCfg, weldGr1Dwg);
	}
	
	@Test
	public void testWeldGr2() throws XmlMappingException, IOException{
    	analyzerService.validateArea(weldGrCfg, weldGr2Dwg);
	}

	@Test
	public void testOld200() throws XmlMappingException, IOException{
    	analyzerService.validateArea(cfgTest200, dwgTest200);
    	analyzerService.buildCutSheetReport(cfgTest200, dwgTest200);
    	Map<Integer, BranchInfo> map = dwgTest200.getAreaCutSheet().getBranchMap();
    	reportingService.renderBranchLabels(cfgTest200, dwgTest200);
    	for(BranchInfo bi : map.values()){
    		int x = 1;
    	}
  //      reportingService.renderSizes(cfgTest200, dwgTest200);
        dwgTest200.setAreaBody(null);
		jaxb2Marshaller.marshal(dwgTest200, new StreamResult(System.out));
	}
	
	@Test
	public void testCell3() throws XmlMappingException, IOException{
    	analyzerService.validateArea(cell3Cfg, cell3Dwg);
    	analyzerService.buildCutSheetReport(cell3Cfg, cell3Dwg);

    	reportingService.renderMainLabels(cell3Cfg, cell3Dwg);
        reportingService.renderSizes(cell3Cfg, cell3Dwg, true, true, true, true);
        cell3Dwg.setAreaBody(null);
		jaxb2Marshaller.marshal(cell3Dwg, new StreamResult(System.out));
	}
*/

}
