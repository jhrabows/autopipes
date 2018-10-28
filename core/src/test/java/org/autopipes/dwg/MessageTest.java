package org.autopipes.dwg;

import junit.framework.TestCase;

public class MessageTest extends TestCase {
	/*
	private Marshaller marshaller;
	@Override
	public void setUp() throws Exception{
		JAXBContext jaxbContext = JAXBContext.newInstance(
			//	MapBean.class,
				Vendor.class,
				Status.class,
				DwgRoot.class);

		marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	}
    private CtDrawing createTestHead()throws Exception{
    	CtDrawing head = new CtDrawing();
    	updateTestHead(head);
   	    return head;
    }
    private void updateTestHead(final CtDrawing head){
    	head.setDwgName("dwg1");
    	head.setDwgTextSize(5.0);
    	//DatatypeFactory df = DatatypeFactory.newInstance();
    	//XMLGregorianCalendar date = df.newXMLGregorianCalendar(new GregorianCalendar());
    	head.setDwgUpdateDate(new GregorianCalendar());
    }
    private Status createTestStatus() throws Exception{
    	Status ret = new Status();
        CtArea a = new CtArea();
        ret.getAreaList().add(a);
        CtEntity e = new CtEntity();
        ret.getErrEntityList().add(e);
    	return ret;
    }
    private DwgRoot createTestConfig() throws Exception{
    	DwgRoot ret = new DwgRoot();
    	updateTestHead(ret);
    	//ret.setDwgHead(createTestHead());
    	CtOptions options = new CtOptions();
    	options.setAngularDelta(0.1);
    	options.setMainCountStart(10);
    	options.getMainCutList().add(10.0);
       	options.getMainCutList().add(20.0);
       	options.getBranchCutList().add(5.0);
   	    ret.setOptionsRoot(options);
   // 	ConfigRoot.LayersRoot layers = new ConfigRoot.LayersRoot();
        CtLayer layer = new CtLayer();
        layer.setName("ML");
        layer.setVendor(Vendor.VIC);
        layer.setSubType(DwgRoot.Attachment.threaded);
        layer.setType(DwgRoot.Designation.Main);
        layer.setMainDiameter(Diameter.D4);
        layer.setHoleDiameter(Diameter.D2);
        layer.getBranchDiameter().put(1, Diameter.D2);
   // 	layers.getLayer().add(layer);
    	options.getLayer().add(layer);

    	return ret;
    }

    private DwgRoot createTestDrawing() throws Exception{
    	DwgRoot ret = new DwgRoot();

    	return ret;
    }

    public void testConfigSerialization() throws Exception{

		DwgRoot root = createTestConfig();
		marshaller.marshal(root, System.out);

    }
    public void testDwgSerialization() throws Exception{

		DwgRoot root = createTestDrawing();
		marshaller.marshal(root, System.out);

    }
    public void testStatus() throws Exception{

		Status s = createTestStatus();
		marshaller.marshal(s, System.out);

    }



    public void testLayer() throws Exception{
    	CtLayer l = new CtLayer();
    	l.getBranchDiameter().put(1, Diameter.D2);
		marshaller.marshal(l, System.out);
    }
    */
}
