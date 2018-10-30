package org.autopipes.springws;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.autopipes.model.DrawingArea;
import org.autopipes.model.FloorDrawing;
import org.autopipes.model.RenderDwg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml"})
public class TestClient {

	@Autowired
	private Marshaller marshaller;
	@Autowired
	private FloorDrawing ocfgTest1;
	@Autowired
	private RenderDwg renTest2;
	@Autowired
	private Resource dwgTest2;
	@Autowired
	private DrawingArea odwgTest2;
	@Autowired
	private WebServiceTemplate webServiceTemplate;

	@Test
	public void testRendering() throws Exception{
        Object reply = webServiceTemplate.marshalSendAndReceive(renTest2);
        FloorDrawing status = (FloorDrawing)reply;
        Result out = new StreamResult(System.out);
        marshaller.marshal(status, out);
	}
	@Test
	public void testLoadDrawing() throws Exception{
        Result out = new StreamResult(System.out);
        Source in = new StreamSource(dwgTest2.getInputStream());
		webServiceTemplate.sendSourceAndReceiveToResult(in, out);
	}
	@Test
	public void testLoadConfig1()throws Exception{
        Object reply = webServiceTemplate.marshalSendAndReceive(ocfgTest1);
        FloorDrawing status = (FloorDrawing)reply;
        Result out = new StreamResult(System.out);
        marshaller.marshal(status, out);
	}
	@Test
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
