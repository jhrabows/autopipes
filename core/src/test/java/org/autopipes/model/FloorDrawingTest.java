package org.autopipes.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.autopipes.model.AreaCutSheet.BranchInfo;
import org.autopipes.model.AreaCutSheet.CutSheetInfo;
import org.autopipes.model.AreaCutSheet.EdgeMultiplicity;
import org.autopipes.takeout.Attachment;
import org.autopipes.takeout.Diameter;
import org.autopipes.takeout.Fitting;
import org.autopipes.takeout.TakeoutInfo;
import org.autopipes.takeout.Vendor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.autopipes.takeout.Fitting.Direction;


public class FloorDrawingTest {
	private static Marshaller marshaller;

	private DwgEntity createCircle(){
		DwgPoint center = new DwgPoint();
		center.setCoordinate(new double[] { 0.0, 0.0 } );
		return DwgEntity.createCircle(center, 1.5);
	}
	private DwgEntity createText(){
		DwgPoint start = new DwgPoint();
		start.setCoordinate(new double[] { 0.0, 0.0 } );
		return DwgEntity.createText(start, 1.5, "Hello");
	}
	private DwgEntity createIdText(){
		DwgPoint start = new DwgPoint();
		start.setCoordinate(new double[] { 0.0, 0.0 } );
		DwgSize size = new DwgSize(10.0, 100.0);
		Long id = 1L;

		return DwgEntity.createMultiText(start, size,
				Arrays.asList(new String[]{"Area", id.toString(), "A-1" }));
	}
    private RenderDwg createRenderDwg(){
    	RenderDwg ret = new RenderDwg();
    	ret.setDrawingId(1L);
    	ret.setAreaId(1L);
//    	ret.setMainDiameters(true);
    	return ret;
    }
	private DrawingArea createDrawingArea() throws Exception{
		DrawingArea ret = new DrawingArea();
		ret.setAreaName("area-1");
		ret.setAreaId(1L);
    	ret.setDrawingId(1L);

    	AreaOptions options = new AreaOptions();
    	options.setBranchLabel("B");
    	options.setMainLabel("M");
    	options.setBranchStartNo(10);
    	options.setMainStartNo(20);
    	

    	ret.setAreaOptions(options);

    	AreaBody body = new AreaBody();
    	ret.setAreaBody(body);
        List<DwgEntity> entities = body.getDwgEntity();
    	DwgEntity circle = createCircle();
    	circle.setLy("Main");
        entities.add(circle);

        List<DwgEntity> rendering = ret.getRendering();
    	DwgEntity txt = createText();
    	txt.setLy("Br");
    	rendering.add(txt);

    	DwgEntity id = createIdText();
    	circle.setLy("Main");
    	rendering.add(id);
    	
    	AreaCutSheet cs = createTestCutSheet();
    	ret.setAreaCutSheet(cs);

		return ret;
	}
    private FloorDrawing createFloorDrawing()/* throws Exception*/{
    	FloorDrawing ret = new FloorDrawing();
    	ret.setDwgName("dwg1");
    	ret.setDwgTextSize(5.0);
    	ret.setDwgUpdateDate(new GregorianCalendar());
    	DrawingOptions options = new DrawingOptions();
    	options.setMainCountStart(10);
    	//options.getMainCutList().add(10.0);
       //	options.getMainCutList().add(20.0);
      // 	options.getBranchCutList().add(5.0);
   	    ret.setOptionsRoot(options);
        DrawingLayer layer = new DrawingLayer();
        layer.setName("ML");
        layer.setVendor(Vendor.VIC);
        layer.setSubType(Attachment.threaded);
        layer.setType(DrawingLayer.Designation.Main);
        layer.setMainDiameter(Diameter.D4);
 //       layer.setHoleDiameter(Diameter.D2);
        layer.getBranchDiameter().put(1, Diameter.D2);
    	options.getLayer().add(layer);
    	return ret;
    }
    @Test
    public void testConfigSerialization() throws Exception{

    	FloorDrawing root = createFloorDrawing();
		marshaller.marshal(root, System.out);

    }
    @Test
    public void testAreaSerialization() throws Exception{

    	DrawingArea root = createDrawingArea();
    	root.getAreaCutSheet().preSerialize();
		marshaller.marshal(root, System.out);

    }
    private Pipe createTestPipe(){
    	Pipe origin = new Pipe();
    	origin.setId(1);
    	origin.setLayerName("B1");
    	origin.setSpan(new BigDecimal(10));
    	PipeAttachment pa = new PipeAttachment();
    	pa.setDirectionInFitting(Direction.E);
    	pa.setTakeout(new BigDecimal(1));
    	origin.setEndAttachment(pa);
    	origin.setStartAttachment(pa);
    	return origin;
    }
    private BranchInfo createTestBranchInfo()
    {
    	BranchInfo bi = new BranchInfo();
    	bi.setBranchId(2);
    	bi.setOrigin(createTestPipe());
    	return bi;
    }
    private CutSheetInfo createCutSheetInfo(){
    	Fitting f = createTestFitting();
    	CutSheetInfo csi = new CutSheetInfo();
    	csi.setPipe(createTestPipe());
    	csi.setStartFitting(f);
    	csi.setEndFitting(f);
    	return csi;
    }
    private  AreaCutSheet createTestCutSheet() throws Exception{
    	AreaCutSheet cs = new AreaCutSheet();
    	BranchInfo bi = createTestBranchInfo();
    	CutSheetInfo csi = createCutSheetInfo();
    	EdgeMultiplicity em = new EdgeMultiplicity();
    	em.setCount(2);
    	em.setEdgeInfo(csi);
		cs.getBranchMap().put(1, bi);
		bi.getEdgeMultiplicity().add(em);
		cs.getMainThreadedList().add(csi);
		cs.preSerialize();
        return cs;
    }
    @Test
    public void testTakeoutInfoSerialization() throws JAXBException{
    	TakeoutInfo ti = new TakeoutInfo();
    	ti.setMain(true);
		marshaller.marshal(ti, System.out);
    }
    @Test
    public void testRenderingSerialization() throws Exception{

    	RenderDwg root = createRenderDwg();
		marshaller.marshal(root, System.out);

    }
    @Test
    public void testFullSerialization() throws Exception{

    	FloorDrawing root = createFloorDrawing();
    	DrawingArea area = createDrawingArea();
    	root.addArea(area);
		marshaller.marshal(root, System.out);

    }

    @BeforeClass
	public static void setUp() throws Exception{
		JAXBContext jaxbContext = JAXBContext.newInstance(
				Vendor.class,
				DrawingArea.class,
				RenderDwg.class,
				FloorDrawing.class,
				TakeoutInfo.class,
				AreaCutSheet.class,
				AreaCutSheet.BranchInfo.class,
				AreaCutSheet.CutSheetInfo.class);

		marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	}
    private static String TEST_FITTING =
    	   "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
    	   +"<ns2:fitting xmlns:ns2=\"http://dwg.autopipes.org\">"
    	       +"<type>Coupling</type>"
    	       +"<attachment>grooved</attachment>"
    	       +"<vendor>FIRELOCK</vendor>"
    	       +"<diameter>D1</diameter><diameter>D2</diameter>"
    	   +"</ns2:fitting>";
    	   
    public Fitting createTestFitting() {  
    	try{
		JAXBContext jaxbContext = JAXBContext.newInstance(
				Fitting.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		InputStream is = new ByteArrayInputStream(
				TEST_FITTING.getBytes("UTF-8"));
		return (Fitting)unmarshaller.unmarshal(is);
    	}catch(Exception e){
    		
    	}
    	return null;
   }

}
