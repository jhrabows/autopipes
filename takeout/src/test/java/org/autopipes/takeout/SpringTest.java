package org.autopipes.takeout;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.log4j.Logger;
import org.autopipes.takeout.TakeoutInfo.Cut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:test-context.xml")
public class SpringTest {
    private static Logger logger = Logger.getLogger(SpringTest.class);
    
	// injected by Spring
    @Autowired
	private TakeoutRepository root;

    @Test
    // Testing deserialization of a test takeout repo which contains made-up data.
	public void testSpringSetup(){
		Assert.assertNotNull(root);
        logger.info(root);
        TakeoutInfo ti = root.takeoutInfo(Diameter.D1);
        Assert.assertNotNull(ti);
        Diameter dl = ti.getDrillLimit(Attachment.mechanical);
        Assert.assertEquals(dl, Diameter.D1);
        BigDecimal val = ti.getGroovedByAngle().get(Angle.deg90).getByVendor().get(Vendor.FIRELOCK);
        Assert.assertEquals(val, new BigDecimal(10));
        BigDecimal val2 = ti.getGroovedByAngle().get(Angle.deg45).getByDiameter().get(Diameter.D1);
        Assert.assertEquals(val2, new BigDecimal(6));
        Cut cut = ti.getByDiameter().get(Diameter.D1);
        val = cut.getByAttachment().get(Attachment.welded);
        Assert.assertEquals(val, new BigDecimal(0.5));
	}


}
