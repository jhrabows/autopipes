package org.autopipes.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.autopipes.model.DrawingArea;
import org.autopipes.model.FloorDrawing;
import org.autopipes.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

/**
 * One could think that this could be done in service @PostConstruct. However that would not have worked:
 * In the @PostConstruct (as with the afterPropertiesSet from the InitializingBean interface)
 * there is no way to ensure that all the post processing is already done, so (indeed) there can be no Transactions.
 * @author jhrabows
 *
 */
@Component
public class StartupInitializer {
    private static final Logger logger = LoggerFactory.getLogger(StartupInitializer.class);
	
	@Autowired
	private StorageService service;
	
    @Value("classpath:dwg/test-dwg-1.xml")
    private Resource dwg1Res;
    @Value("classpath:dwg/test-cfg-1.xml")
    private Resource cfg1Res;

	@Autowired
	private Jaxb2Marshaller jaxb2Marshaller;
	
	private Object unmarshalResource(Resource res) throws IOException {
		InputStream is = res.getInputStream();
    	Reader reader = new InputStreamReader(is);
    	Source source = new StreamSource(reader);
    	return jaxb2Marshaller.unmarshal(source);
	}

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
    	logger.info("Initializing static drawings");
    	try{
         	FloorDrawing cfg1 = (FloorDrawing) unmarshalResource(cfg1Res);
        	service.mergeDrawing(cfg1);
        	Long id = cfg1.getId();
        	FloorDrawing cfg1Dwg = service.findOneDrawing(id);
            jaxb2Marshaller.marshal(cfg1Dwg, new StreamResult(System.out));
            DrawingArea dwg1 = (DrawingArea) unmarshalResource(dwg1Res);
            service.mergeArea(dwg1);
     	}catch(Exception e){
    		logger.error("Failed to initialize static drawings", e);
    	}
    	
    }

}
