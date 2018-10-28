package org.autopipes.springws;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.autopipes.model.DrawingArea;
import org.autopipes.model.FloorDrawing;
import org.autopipes.model.RenderDwg;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.ws.client.core.WebServiceTemplate;

public class TestClient  extends AbstractDependencyInjectionSpringContextTests{
	@Override
	protected String[] getConfigLocations() {
	        return new String[]{"test-context.xml"};
	    }

	private Marshaller marshaller;

	private FloorDrawing ocfgTest1;
	private RenderDwg renTest2;

	private Resource dwgTest2;
	private DrawingArea odwgTest2;

	private WebServiceTemplate webServiceTemplate;

	public TestClient(){
	    setAutowireMode(AbstractDependencyInjectionSpringContextTests.AUTOWIRE_BY_NAME);
	}

	public void testRendering() throws Exception{
        Object reply = webServiceTemplate.marshalSendAndReceive(renTest2);
        FloorDrawing status = (FloorDrawing)reply;
        Result out = new StreamResult(System.out);
        marshaller.marshal(status, out);
	}

	public void testLoadDrawing() throws Exception{
        Result out = new StreamResult(System.out);
        Source in = new StreamSource(dwgTest2.getInputStream());
		webServiceTemplate.sendSourceAndReceiveToResult(in, out);
	}

	public void testLoadConfig1()throws Exception{
        Object reply = webServiceTemplate.marshalSendAndReceive(ocfgTest1);
        FloorDrawing status = (FloorDrawing)reply;
        Result out = new StreamResult(System.out);
        marshaller.marshal(status, out);
	}
	public void testLoadDrawingObj()throws Exception{
        Object reply = webServiceTemplate.marshalSendAndReceive(ocfgTest1);
        FloorDrawing cfgStatus = (FloorDrawing)reply;
        long cfgId = cfgStatus.getId();
        odwgTest2.setDrawingId(cfgId);
        reply = webServiceTemplate.marshalSendAndReceive(odwgTest2);
        DrawingArea status = (DrawingArea)reply;
        Result out = new StreamResult(System.out);
        marshaller.marshal(status, out);
	}

	public FloorDrawing getOcfgTest1() {
		return ocfgTest1;
	}

	public void setOcfgTest1(final FloorDrawing ocfgTest1) {
		this.ocfgTest1 = ocfgTest1;
	}

	public Marshaller getJaxb2Marshaller() {
		return marshaller;
	}

	public void setJaxb2Marshaller(final Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public WebServiceTemplate getWebServiceTemplate() {
		return webServiceTemplate;
	}

	public void setWebServiceTemplate(final WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}

	public Resource getDwgTest2() {
		return dwgTest2;
	}

	public void setDwgTest2(final Resource dwgTest2) {
		this.dwgTest2 = dwgTest2;
	}

	public DrawingArea getOdwgTest2() {
		return odwgTest2;
	}

	public void setOdwgTest2(final DrawingArea odwgTest2) {
		this.odwgTest2 = odwgTest2;
	}

	public RenderDwg getRenTest2() {
		return renTest2;
	}

	public void setRenTest2(final RenderDwg renTest2) {
		this.renTest2 = renTest2;
	}

}
