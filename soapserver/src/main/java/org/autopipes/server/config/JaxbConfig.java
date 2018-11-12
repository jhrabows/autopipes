package org.autopipes.server.config;

import java.util.HashMap;

import org.autopipes.model.AreaBody;
import org.autopipes.model.DrawingArea;
import org.autopipes.model.DrawingOptions;
import org.autopipes.model.FloorDrawing;
import org.autopipes.model.RenderDwg;
import org.autopipes.takeout.TakeoutRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class JaxbConfig {
	
	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		final Jaxb2Marshaller ret = new Jaxb2Marshaller();
		ret.setClassesToBeBound(FloorDrawing.class,
				DrawingOptions.class,
				DrawingArea.class,
				AreaBody.class,
				RenderDwg.class,
				TakeoutRepository.class);
		ret.setMarshallerProperties(new HashMap<String, Object>(){{
			put("jaxb.formatted.output", true);
		}});
		return ret;
	}
}
