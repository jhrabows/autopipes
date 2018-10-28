package org.autopipes.service;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.autopipes.model.AreaBody;
import org.autopipes.model.DrawingArea;
import org.autopipes.model.DwgEntity;
import org.autopipes.model.DwgPoint;
import org.autopipes.model.FloorDrawing;
import org.autopipes.model.Pipe;
import org.autopipes.model.PipeFitting;
import org.autopipes.model.RenderDwg;
import org.autopipes.model.AreaBody.Defect;
import org.autopipes.model.AreaBody.PointInfo;
import org.autopipes.model.AreaCutSheet.BranchInfo;
import org.autopipes.model.DrawingArea.Readiness;
import org.autopipes.takeout.Fitting;
import org.autopipes.util.EdgeIterator;
import org.autopipes.util.EdgeTrimmer;
import org.jgrapht.UndirectedGraph;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class AnalyzerServiceTest extends
		AbstractDependencyInjectionSpringContextTests {

	private AnalyzerService analyzerService;
	private ReportingService reportingService;
	private Marshaller jaxb2Marshaller;

	private FloorDrawing cfgTest1;
    private DrawingArea dwgTest1;
    private DrawingArea dwgTest3;
    private DrawingArea dwgTestDiscon;
    private DrawingArea dwgTestBreak;
    private DrawingArea dwgTestSnap;
    private DrawingArea dwgTestJmp;
    private DrawingArea dwgTestP;
    private DrawingArea dwgTestPT;
	private DrawingArea dwgTestP2;
    private DrawingArea dwgTestErr1;
    private DrawingArea dwgTestErr2;
    private DrawingArea dwgTestEll45;
    private DrawingArea dwgTestMainEll45;

	private DrawingArea dwgTestLongBr;
    private DrawingArea dwgTestLab;
    private DrawingArea dwgTestLongM;
    
    private DrawingArea dwgTestMatch;
    private DrawingArea dwgTestMatch2;
    private DrawingArea dwgTestMatch3;
    private DrawingArea dwgTestMatchReducer;
    
    private DrawingArea dwgTestTake1;
	private FloorDrawing cfgTestTake1;

	private DrawingArea dwgTestCross;
	private FloorDrawing cfgTestCross;


	private DrawingArea dwgTest200;
	private FloorDrawing cfgTest200;

	private FloorDrawing cfgTest300;
	private DrawingArea dwgTest300;
	
	public DrawingArea getDwgTest200() {
		return dwgTest200;
	}

	public void setDwgTest200(DrawingArea dwgTest200) {
		this.dwgTest200 = dwgTest200;
	}

	public FloorDrawing getCfgTest200() {
		return cfgTest200;
	}

	public void setCfgTest200(FloorDrawing cfgTest200) {
		this.cfgTest200 = cfgTest200;
	}


	public DrawingArea getDwgTestMainTakeout() {
		return dwgTestMainTakeout;
	}

	public void setDwgTestMainTakeout(DrawingArea dwgTestMainTakeout) {
		this.dwgTestMainTakeout = dwgTestMainTakeout;
	}

	public FloorDrawing getCfgTestMainTakeout() {
		return cfgTestMainTakeout;
	}

	public void setCfgTestMainTakeout(FloorDrawing cfgTestMainTakeout) {
		this.cfgTestMainTakeout = cfgTestMainTakeout;
	}

	private DrawingArea dwgTestMainTakeout;
	private FloorDrawing cfgTestMainTakeout;
	
	public FloorDrawing getCfgTest300() {
		return cfgTest300;
	}

	public void setCfgTest300(FloorDrawing cfgTest300) {
		this.cfgTest300 = cfgTest300;
	}

	public DrawingArea getDwgTest300() {
		return dwgTest300;
	}

	public void setDwgTest300(DrawingArea dwgTest300) {
		this.dwgTest300 = dwgTest300;
	}



    public DrawingArea getDwgTestMatchReducer() {
		return dwgTestMatchReducer;
	}

	public void setDwgTestMatchReducer(DrawingArea dwgTestMatchReducer) {
		this.dwgTestMatchReducer = dwgTestMatchReducer;
	}

	public DrawingArea getDwgTestMatch3() {
		return dwgTestMatch3;
	}

	public void setDwgTestMatch3(DrawingArea dwgTestMatch3) {
		this.dwgTestMatch3 = dwgTestMatch3;
	}

	private DrawingArea dwgTestVendors;
    private DrawingArea dwgTestMaincut;

	public DrawingArea getDwgTestMaincut() {
		return dwgTestMaincut;
	}

	public void setDwgTestMaincut(DrawingArea dwgTestMaincut) {
		this.dwgTestMaincut = dwgTestMaincut;
	}

	public DrawingArea getDwgTestMatch2() {
		return dwgTestMatch2;
	}

	public void setDwgTestMatch2(DrawingArea dwgTestMatch2) {
		this.dwgTestMatch2 = dwgTestMatch2;
	}

	private FloorDrawing cfgTestMatch;


	private DrawingArea dwgTestSimpleM;
    private RenderDwg renTest1;
    private RenderDwg renTest2;
    private DrawingArea dwgTestTwin;
    private DrawingArea dwgTestBlock;
    private FloorDrawing cfgTestTwin;
    private FloorDrawing cfgTestZ1;
    private DrawingArea dwgTestZ1;

    public DrawingArea getDwgTestMainEll45() {
		return dwgTestMainEll45;
	}

	public void setDwgTestMainEll45(DrawingArea dwgTestMainEll45) {
		this.dwgTestMainEll45 = dwgTestMainEll45;
	}

	public AnalyzerServiceTest(){
    	super();
	    setAutowireMode(AbstractDependencyInjectionSpringContextTests.AUTOWIRE_BY_NAME);
    }

	@Override
	protected String[] getConfigLocations() {
	    return new String[]{"test-app-context.xml",
	    		"test-pipe-context.xml"};
	    }

	public void testSpringSetup()throws Exception{
        Result res = new StreamResult(System.out);
        assertNotNull(cfgTest1);
        cfgTest1.getOptionsRoot().preSerialize();
        jaxb2Marshaller.marshal(cfgTest1, res);
        assertNotNull(dwgTest1);
        jaxb2Marshaller.marshal(dwgTest1, res);
        assertNotNull(renTest2);
        jaxb2Marshaller.marshal(renTest2, res);
	}

	public void testTrimmerP(){
		analyzerService.validateArea(cfgTest1, dwgTestPT);
		PipeFitting raiser = dwgTestPT.getAreaBody().getRaiser();
		assertNotNull(raiser);
		EdgeTrimmer<PipeFitting, Pipe> trimmer = new EdgeTrimmer<PipeFitting, Pipe>(
		    dwgTestPT.getAreaBody().getPipeGraph(), raiser);
		assertEquals(4, trimmer.getCoreSubgraph().vertexSet().size());
		assertEquals(0, trimmer.getTreeMap().keySet().size());
		UndirectedGraph<PipeFitting, Pipe> tree = trimmer.getRootTree();
		assertEquals(2, tree.vertexSet().size());
		assertNotNull(trimmer.getRootBase());
		assertNotNull(tree.getEdge(raiser, trimmer.getRootBase()));
	}
	public void testTeeJump()throws Exception{
		analyzerService.validateArea(cfgTestTake1, dwgTestTake1);
		analyzerService.buildCutSheetReport(cfgTestTake1, dwgTestTake1);
		dwgTestTake1.getAreaCutSheet().preSerialize();
        jaxb2Marshaller.marshal(dwgTestTake1, new StreamResult(System.out));
        for(PointInfo pi : dwgTestTake1.getAreaBody().getPointMap().values()){
        	System.out.println(pi);
        }
        reportingService.renderBranchLabels(cfgTestTake1, dwgTestTake1);
        reportingService.renderBreakups(cfgTestTake1, dwgTestTake1, true, true);
        reportingService.renderSizes(cfgTestTake1, dwgTestTake1, true, true, true, true);
        Result res = new StreamResult(System.out);
        jaxb2Marshaller.marshal(dwgTestTake1, res);
	}

	public void testCross()throws Exception{
		analyzerService.validateArea(cfgTestCross, dwgTestCross);
		analyzerService.buildCutSheetReport(cfgTestCross, dwgTestCross);
		dwgTestCross.getAreaCutSheet().preSerialize();
        jaxb2Marshaller.marshal(dwgTestCross, new StreamResult(System.out));
        for(PointInfo pi : dwgTestCross.getAreaBody().getPointMap().values()){
        	System.out.println(pi);
        }
        reportingService.renderBranchLabels(cfgTestCross, dwgTestCross);
        reportingService.renderBreakups(cfgTestCross, dwgTestCross, true, true);
        reportingService.renderSizes(cfgTestCross, dwgTestCross, true, true, true, true);
        Result res = new StreamResult(System.out);
        jaxb2Marshaller.marshal(dwgTestCross, res);
	}

	public void testLongBranch()throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTestLongBr);
		analyzerService.buildCutSheetReport(cfgTest1, dwgTestLongBr);
		dwgTestLongBr.getAreaCutSheet().preSerialize();
		//for(DwgEntity e : dwgTestLongBr.getAreaBody().getEdgesInOrder()){
        //	System.out.println(e + ": " + e.getEntStart() + "-" + e.getEntEnd());
        //}
		assertEquals(3, dwgTestLongBr.getAreaBody().getEdgesInOrder().size());
        jaxb2Marshaller.marshal(dwgTestLongBr, new StreamResult(System.out));
        //for(DwgEntity e : dwgTestLongBr.getAreaBody().getEdgesInOrder()){
        //	System.out.println(dwgTestLongBr.getAreaBody().getEdgeInfo(e));
        //}
        for(PointInfo pi : dwgTestLongBr.getAreaBody().getPointMap().values()){
        	System.out.println(pi);
        }
        reportingService.renderBranchLabels(cfgTest1, dwgTestLongBr);
        reportingService.renderBreakups(cfgTest1, dwgTestLongBr, true, true);
        reportingService.renderSizes(cfgTest1, dwgTestLongBr, true, true, true, true);
        Result res = new StreamResult(System.out);
        jaxb2Marshaller.marshal(dwgTestLongBr, res);
	}
	public void testLab()throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTestLab);
		analyzerService.buildCutSheetReport(cfgTest1, dwgTestLab);
        reportingService.renderBranchLabels(cfgTest1, dwgTestLab);
        jaxb2Marshaller.marshal(dwgTestLab, new StreamResult(System.out));
        assertEquals(2, dwgTestLab.getRendering().size());
	}
	public void testLongMain(){
		analyzerService.validateArea(cfgTest1, dwgTestLongM);
		//for(DwgEntity e : dwgTestLongM.getAreaBody().getEdgesInOrder()){
        //	System.out.println(e + ": " + e.getEntStart() + "-" + e.getEntEnd());
        //}
		assertEquals(4, dwgTestLongM.getAreaBody().getEdgesInOrder().size());
	}
	public void testMatch() throws XmlMappingException, IOException{
		analyzerService.validateArea(cfgTestMatch, dwgTestMatch);
		assertEquals(Readiness.Ready, dwgTestMatch.getAreaReadiness());
		analyzerService.buildCutSheetReport(cfgTestMatch, dwgTestMatch);
		dwgTestMatch.setAreaBody(null);
		dwgTestMatch.setAreaOptions(null);
		jaxb2Marshaller.marshal(dwgTestMatch, new StreamResult(System.out));
		for(BranchInfo bi : dwgTestMatch.getAreaCutSheet().getBranchMap().values()){
			assertEquals(3, bi.getEdgeMultiplicity().size());
		}
	}
	public void testMatch2() throws XmlMappingException, IOException{
		analyzerService.validateArea(cfgTestMatch, dwgTestMatch2);
		assertEquals(Readiness.Ready, dwgTestMatch2.getAreaReadiness());
		analyzerService.buildCutSheetReport(cfgTestMatch, dwgTestMatch2);
		dwgTestMatch2.setAreaBody(null);
		dwgTestMatch2.setAreaOptions(null);
		dwgTestMatch2.getAreaCutSheet().preSerialize();
		jaxb2Marshaller.marshal(dwgTestMatch2, new StreamResult(System.out));
		for(BranchInfo bi : dwgTestMatch2.getAreaCutSheet().getBranchMap().values()){
			assertEquals(2, bi.getEdgeMultiplicity().size());
		}
	}
	public void testMatch3() throws XmlMappingException, IOException{
		analyzerService.validateArea(cfgTestMatch, dwgTestMatch3);
	}
	public void testMatchReducer() throws XmlMappingException, IOException{
		analyzerService.validateArea(cfgTestMatch, dwgTestMatchReducer);
	    reportingService.renderAreaStatus(cfgTestMatch, dwgTestMatchReducer);
		dwgTestMatchReducer.setAreaBody(null);
		dwgTestMatchReducer.setAreaCutSheet(null);
		jaxb2Marshaller.marshal(dwgTestMatchReducer, new StreamResult(System.out));

		assertEquals(Readiness.Ready, dwgTestMatchReducer.getAreaReadiness());
	}

	public void testVendors(){
		analyzerService.validateArea(cfgTestMatch, dwgTestVendors);
		assertEquals(Readiness.Ready, dwgTestVendors.getAreaReadiness());
	}
	public void testMaincut() throws XmlMappingException, IOException{
		analyzerService.validateArea(cfgTestMatch, dwgTestMaincut);
		assertEquals(Readiness.Ready, dwgTestMaincut.getAreaReadiness());
    	analyzerService.buildCutSheetReport(cfgTestMatch, dwgTestMaincut);

        reportingService.renderSizes(cfgTestMatch, dwgTestMaincut, true, true, true, true);
        dwgTestMaincut.setAreaBody(null);
        dwgTestMaincut.setAreaCutSheet(null);
		jaxb2Marshaller.marshal(dwgTestMaincut, new StreamResult(System.out));
	}

	public void test300() throws XmlMappingException, IOException{
		analyzerService.validateArea(cfgTest300, dwgTest300);
		assertEquals(Readiness.Ready, dwgTest300.getAreaReadiness());
    	analyzerService.buildCutSheetReport(cfgTest300, dwgTest300);

        reportingService.renderSizes(cfgTest300, dwgTest300, true, true, true, true);
        dwgTest300.setAreaBody(null);
        dwgTest300.setAreaCutSheet(null);
		jaxb2Marshaller.marshal(dwgTest300, new StreamResult(System.out));
	}

	public void test200() throws XmlMappingException, IOException{
		analyzerService.validateArea(cfgTest200, dwgTest200);
		assertEquals(Readiness.Ready, dwgTest200.getAreaReadiness());
    	analyzerService.buildCutSheetReport(cfgTest200, dwgTest200);

        reportingService.renderSizes(cfgTest200, dwgTest200, true, true, true, true);
        dwgTest200.setAreaBody(null);
		jaxb2Marshaller.marshal(dwgTest200, new StreamResult(System.out));
	}

	
	//dwgTestMainTakeout
	public void testMainTakeout() throws Exception{
		analyzerService.validateArea(cfgTestMainTakeout, dwgTestMainTakeout);
		analyzerService.buildCutSheetReport(cfgTestMainTakeout, dwgTestMainTakeout);
        reportingService.renderMainLabels(cfgTestMainTakeout, dwgTestMainTakeout);
        reportingService.renderSizes(cfgTestMainTakeout, dwgTestMainTakeout, true, true, true, true);
		jaxb2Marshaller.marshal(dwgTestMainTakeout, new StreamResult(System.out));
	}

	public void testSimpleMain() throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTestSimpleM);
		analyzerService.buildCutSheetReport(cfgTest1, dwgTestSimpleM);
		assertEquals(2, dwgTestSimpleM.getAreaBody().getEdgesInOrder().size());
        reportingService.renderMainLabels(cfgTest1, dwgTestSimpleM);
        reportingService.renderSizes(cfgTest1, dwgTestSimpleM, true, true, true, true);
		jaxb2Marshaller.marshal(dwgTestSimpleM, new StreamResult(System.out));
	}
	public void iteratorTest(final FloorDrawing cfg, final DrawingArea dwg){
		analyzerService.validateArea(cfg, dwg);
		UndirectedGraph<PipeFitting, Pipe> g = dwg.getAreaBody().getPipeGraph();
		PipeFitting raiser = dwg.getAreaBody().getRaiser();
		assertNotNull(raiser);
		EdgeIterator<PipeFitting, Pipe> ei = new EdgeIterator<PipeFitting, Pipe>(g, raiser, PipeFitting.class);
        ei.orderGraph();
	//	for(DwgEntity e : ei.getEdges()){
    //    	System.out.println(e + ": " + g.getEdgeSource(e) + "-" + g.getEdgeTarget(e));
    //    }
        assertEquals(ei.getEdges().size(), g.edgeSet().size());
	}
	public void testIteratorP2(){
		iteratorTest(cfgTest1, dwgTestP2);
	}

	public void testIteratorP(){
		iteratorTest(cfgTest1, dwgTestP);
		/*
		analyzerService.validateArea(cfgTest1, dwgTestP);
		DwgPoint raiser = dwgTestP.getAreaBody().getRaiser();
		assertNotNull(raiser);
		EdgeIterator<DwgPoint, DwgEntity> ei = new EdgeIterator<DwgPoint, DwgEntity>(dwgTestP.getAreaBody().getPointGraph(), raiser, DwgPoint.class);
        assertEquals(ei.getEdges().size(), 5);
        for(DwgEntity e : ei.getEdges()){
        	System.out.println(e + ": " + e.getEntStart() + "-" + e.getEntEnd());
        }*/
	}
	public void testError1() throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTestErr1);
		dwgTestErr1.setDefectCount(dwgTestErr1.getAreaBody().getProblemPointCount());

       for(AreaBody.PointInfo ei : dwgTestErr1.getAreaBody().getPointMap().values()){
            Defect d = ei.getStatus();
            if(d != Defect.noDefects){
        	System.out.println(d);
            }
        }
		reportingService.renderAreaStatus(cfgTest1, dwgTestErr1);
		/*for(DwgEntity e : dwgTestErr1.getRendering()){
        	System.out.println(e);
        }*/
        jaxb2Marshaller.marshal(dwgTestErr1, new StreamResult(System.out));
		assertEquals(2, dwgTestErr1.getRendering().size());
	}
	public void testDwg3() throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTest3);
   /*     for(AreaBody.EdgeInfo ei : dwgTest3.getAreaBody().getEdgeInfoMap().values()){
            Defect d = ei.getStatus();
            if(d != Defect.noDefects){
        	System.out.println(d);
            }
        }*/
		reportingService.renderAreaStatus(cfgTest1, dwgTest3);
		for(DwgEntity e : dwgTest3.getRendering()){
        	System.out.println(e);
        }
	}
	public void testError2() throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTestErr2);
		System.out.println(dwgTestErr2.getAreaBody().getPipeGraph());
    /*    for(AreaBody.EdgeInfo ei : dwgTestErr2.getAreaBody().getEdgeInfoMap().values()){
            Defect d = ei.getStatus();
            if(d != Defect.noDefects){
        	System.out.println(d);
            }
        }*/

        for(PointInfo info : dwgTestErr2.getAreaBody().getPointMap().values()){
        	if(info.getStatus() != Defect.noDefects){
        	  System.out.println(info.getStatus());
        	}
        }
		reportingService.renderAreaStatus(cfgTest1, dwgTestErr2);
		for(DwgEntity e : dwgTestErr2.getRendering()){
        	System.out.println(e);
        }
		assertEquals(1, dwgTestErr2.getRendering().size());
        jaxb2Marshaller.marshal(dwgTestErr2, new StreamResult(System.out));
	}

	public void testJump() throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTestJmp);
		analyzerService.buildCutSheetReport(cfgTest1, dwgTestJmp);
        for(PointInfo info : dwgTestJmp.getAreaBody().getPointMap().values()){
    		if(!info.getJumps().isEmpty()){
    			assertEquals(2, info.getFittings().size());
        	    for(PipeFitting f : info.getFittings()){
        			assertEquals(Fitting.Type.Ell, f.getFitting().getType());
        		}
            }
        }
        reportingService.renderSizes(cfgTest1, dwgTestJmp, true, true, true, true);
        reportingService.renderMainLabels(cfgTest1, dwgTestJmp);
        jaxb2Marshaller.marshal(dwgTestJmp, new StreamResult(System.out));
	}
        public void testDisconnected() throws Exception{
		AreaBody areaBody = dwgTestDiscon.getAreaBody();
		analyzerService.validateArea(cfgTest1, dwgTestDiscon);
		assertEquals(2, areaBody.getCenters().size());
		reportingService.renderAreaStatus(cfgTest1, dwgTestDiscon);
		assertEquals(Readiness.Disconnected, dwgTestDiscon.getAreaReadiness());
		jaxb2Marshaller.marshal(dwgTestDiscon, new StreamResult(System.out));
	}
	public void testBreakup() throws Exception{
		// check if a vertex is created when a line touches another in the middle.
		// this area is not valid
		DwgPoint cut = null;
		for(DwgEntity e : dwgTestBreak.getAreaBody().getDwgEntity()){
			if(e.getLy().equals("BL")){
				cut = e.getEntStart();
				break;
			}
		}
		analyzerService.validateArea(cfgTest1, dwgTestBreak);
		jaxb2Marshaller.marshal(dwgTestBreak, new StreamResult(System.out));
        for(AreaBody.PointInfo ei : dwgTestBreak.getAreaBody().getPointMap().values()){
            Defect d = ei.getStatus();
            if(d != Defect.noDefects){
        	System.out.println(d);
            }
        }
        PointInfo pi = dwgTestBreak.getAreaBody().getPointInfo(cut);
		assertEquals(dwgTestBreak.getAreaBody().getPipeGraph().edgeSet().size(), 3);
		assertEquals(dwgTestBreak.getAreaBody().getPipeGraph().degreeOf(pi.getFittings().get(0)), 3);
	}
	public void testSnap() throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTestSnap);
		System.out.println(dwgTestSnap.getAreaBody().getPipeGraph());
		assertEquals(dwgTestSnap.getAreaBody().getPipeGraph().vertexSet().size(), 3);
	}
	public void testZ1() throws Exception{
		analyzerService.validateArea(cfgTestZ1, dwgTestZ1);
		assertEquals(Readiness.Ready, dwgTestZ1.getAreaReadiness());
		//dwgTestZ1.countEdgeMultiplicity();
	}
	public void testMain45() throws Exception{
		analyzerService.validateArea(cfgTest1, dwgTestMainEll45);
		assertEquals(Readiness.Ready, dwgTestMainEll45.getAreaReadiness());
		//dwgTestZ1.countEdgeMultiplicity();
	}
	
	public void testTwin() throws Exception{
		analyzerService.validateArea(cfgTestTwin, dwgTestTwin);
		analyzerService.buildCutSheetReport(cfgTestTwin, dwgTestTwin);
		assertEquals(1, dwgTestTwin.getAreaCutSheet().getBranchMap().size());
		reportingService.renderBranchLabels(cfgTestTwin, dwgTestTwin);
		reportingService.renderSizes(cfgTestTwin, dwgTestTwin, true, true, true, true);
		/*
		List<CutSheetInfo> ret = reportingService.getEdgeInfoForAreaLayer(dwgTestTwin,
				cfgTestTwin.getOptionsRoot().findLayer("BL"));
		for(CutSheetInfo info : ret){
			System.out.println(info.getPipe() +", Attach=" + info.getStartFitting().getAttachment());
		}*/
		dwgTestTwin.setAreaBody(null);
		jaxb2Marshaller.marshal(dwgTestTwin, new StreamResult(System.out));
	}
	public void testBlock() throws Exception{
		analyzerService.validateArea(cfgTestTwin, dwgTestBlock);
		reportingService.renderAreaStatus(cfgTestTwin, dwgTestBlock);
		dwgTestBlock.setAreaBody(null);
		jaxb2Marshaller.marshal(dwgTestBlock, new StreamResult(System.out));
	}
	public AnalyzerService getAnalyzerService() {
		return analyzerService;
	}

	public void setAnalyzerService(final AnalyzerService analyzerService) {
		this.analyzerService = analyzerService;
	}

	public DrawingArea getDwgTest1() {
		return dwgTest1;
	}

	public void setDwgTest1(final DrawingArea dwgTest1) {
		this.dwgTest1 = dwgTest1;
	}

	public Marshaller getJaxb2Marshaller() {
		return jaxb2Marshaller;
	}

	public void setJaxb2Marshaller(final Marshaller jaxb2Marshaller) {
		this.jaxb2Marshaller = jaxb2Marshaller;
	}

	public DrawingArea getDwgTestDiscon() {
		return dwgTestDiscon;
	}

	public void setDwgTestDiscon(final DrawingArea dwgTestDiscon) {
		this.dwgTestDiscon = dwgTestDiscon;
	}

	public FloorDrawing getCfgTest1() {
		return cfgTest1;
	}

	public void setCfgTest1(final FloorDrawing cfgTest1) {
		this.cfgTest1 = cfgTest1;
	}

	public DrawingArea getDwgTestBreak() {
		return dwgTestBreak;
	}

	public void setDwgTestBreak(final DrawingArea dwgTestBreak) {
		this.dwgTestBreak = dwgTestBreak;
	}

	public DrawingArea getDwgTestSnap() {
		return dwgTestSnap;
	}

	public void setDwgTestSnap(final DrawingArea dwgTestSnap) {
		this.dwgTestSnap = dwgTestSnap;
	}

	public DrawingArea getDwgTestP() {
		return dwgTestP;
	}

	public void setDwgTestP(final DrawingArea dwgTestP) {
		this.dwgTestP = dwgTestP;
	}

	public DrawingArea getDwgTestP2() {
		return dwgTestP2;
	}

	public void setDwgTestP2(final DrawingArea dwgTestP2) {
		this.dwgTestP2 = dwgTestP2;
	}

	public DrawingArea getDwgTestErr1() {
		return dwgTestErr1;
	}

	public void setDwgTestErr1(final DrawingArea dwgTestErr1) {
		this.dwgTestErr1 = dwgTestErr1;
	}

	public DrawingArea getDwgTestLongBr() {
		return dwgTestLongBr;
	}

	public void setDwgTestLongBr(final DrawingArea dwgTestLongBr) {
		this.dwgTestLongBr = dwgTestLongBr;
	}

	public DrawingArea getDwgTestLongM() {
		return dwgTestLongM;
	}

	public void setDwgTestLongM(final DrawingArea dwgTestLongM) {
		this.dwgTestLongM = dwgTestLongM;
	}

	public ReportingService getReportingService() {
		return reportingService;
	}

	public void setReportingService(final ReportingService reportingService) {
		this.reportingService = reportingService;
	}

	public DrawingArea getDwgTestErr2() {
		return dwgTestErr2;
	}

	public void setDwgTestErr2(final DrawingArea dwgTestErr2) {
		this.dwgTestErr2 = dwgTestErr2;
	}

	public DrawingArea getDwgTestJmp() {
		return dwgTestJmp;
	}

	public void setDwgTestJmp(final DrawingArea dwgTestJmp) {
		this.dwgTestJmp = dwgTestJmp;
	}

	public DrawingArea getDwgTest3() {
		return dwgTest3;
	}

	public void setDwgTest3(final DrawingArea dwgTest3) {
		this.dwgTest3 = dwgTest3;
	}

	public DrawingArea getDwgTestSimpleM() {
		return dwgTestSimpleM;
	}

	public void setDwgTestSimpleM(final DrawingArea dwgTestSimpleM) {
		this.dwgTestSimpleM = dwgTestSimpleM;
	}

	public RenderDwg getRenTest1() {
		return renTest1;
	}

	public void setRenTest1(final RenderDwg renTest1) {
		this.renTest1 = renTest1;
	}

	public RenderDwg getRenTest2() {
		return renTest2;
	}

	public void setRenTest2(final RenderDwg renTest2) {
		this.renTest2 = renTest2;
	}

	public DrawingArea getDwgTestTwin() {
		return dwgTestTwin;
	}

	public void setDwgTestTwin(final DrawingArea dwgTestTwin) {
		this.dwgTestTwin = dwgTestTwin;
	}

	public FloorDrawing getCfgTestTwin() {
		return cfgTestTwin;
	}

	public void setCfgTestTwin(final FloorDrawing cfgTestTwin) {
		this.cfgTestTwin = cfgTestTwin;
	}

	public DrawingArea getDwgTestLab() {
		return dwgTestLab;
	}

	public void setDwgTestLab(final DrawingArea dwgTestLab) {
		this.dwgTestLab = dwgTestLab;
	}

	public DrawingArea getDwgTestEll45() {
		return dwgTestEll45;
	}

	public void setDwgTestEll45(final DrawingArea dwgTestEll45) {
		this.dwgTestEll45 = dwgTestEll45;
	}

	public DrawingArea getDwgTestBlock() {
		return dwgTestBlock;
	}

	public void setDwgTestBlock(final DrawingArea dwgTestBlock) {
		this.dwgTestBlock = dwgTestBlock;
	}

	public FloorDrawing getCfgTestZ1() {
		return cfgTestZ1;
	}

	public void setCfgTestZ1(final FloorDrawing cfgTestZ1) {
		this.cfgTestZ1 = cfgTestZ1;
	}

	public DrawingArea getDwgTestZ1() {
		return dwgTestZ1;
	}

	public void setDwgTestZ1(final DrawingArea dwgTestZ1) {
		this.dwgTestZ1 = dwgTestZ1;
	}
    public DrawingArea getDwgTestMatch() {
		return dwgTestMatch;
	}

	public void setDwgTestMatch(DrawingArea dwgTestMatch) {
		this.dwgTestMatch = dwgTestMatch;
	}
	public FloorDrawing getCfgTestMatch() {
		return cfgTestMatch;
	}

	public void setCfgTestMatch(FloorDrawing cfgTestMatch) {
		this.cfgTestMatch = cfgTestMatch;
	}
    public DrawingArea getDwgTestPT() {
		return dwgTestPT;
	}

	public void setDwgTestPT(DrawingArea dwgTestPT) {
		this.dwgTestPT = dwgTestPT;
	}
    public DrawingArea getDwgTestVendors() {
		return dwgTestVendors;
	}
	public void setDwgTestVendors(DrawingArea dwgTestVendors) {
		this.dwgTestVendors = dwgTestVendors;
	}

	public DrawingArea getDwgTestTake1() {
		return dwgTestTake1;
	}

	public void setDwgTestTake1(DrawingArea dwgTestTake1) {
		this.dwgTestTake1 = dwgTestTake1;
	}

	public FloorDrawing getCfgTestTake1() {
		return cfgTestTake1;
	}

	public void setCfgTestTake1(FloorDrawing cfgTestTake1) {
		this.cfgTestTake1 = cfgTestTake1;
	}

	public DrawingArea getDwgTestCross() {
		return dwgTestCross;
	}

	public void setDwgTestCross(DrawingArea dwgTestCross) {
		this.dwgTestCross = dwgTestCross;
	}

	public FloorDrawing getCfgTestCross() {
		return cfgTestCross;
	}

	public void setCfgTestCross(FloorDrawing cfgTestCross) {
		this.cfgTestCross = cfgTestCross;
	}
}
