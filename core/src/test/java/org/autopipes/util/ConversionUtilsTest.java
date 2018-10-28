package org.autopipes.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class ConversionUtilsTest {
	
	@Test
	public void testMixed(){
		assertEquals(14, ConversionUtils.string2inches("1'2\"").longValue());
	}

	@Test
	public void testFoot(){
		assertEquals(24, ConversionUtils.string2inches("2'").longValue());
	}
	
	@Test
	public void testInch(){
		assertEquals(5, ConversionUtils.string2inches("5\"").longValue());
	}
	
	@Test
	public void testMinusFoot(){
		assertEquals(-24, ConversionUtils.string2inches("-2'").longValue());
	}
	@Test
	public void testMinusInch(){
		assertEquals(-3, ConversionUtils.string2inches("-3\"").longValue());
	}
	
	@Test
	public void testInvalid(){
		assertNull(ConversionUtils.string2inches("10.5"));
	}

}
