package org.autopipes.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.autopipes.model.DwgPoint;
import org.autopipes.util.PlaneGeo.Divider;
import org.autopipes.util.PlaneGeo.Point;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PlaneGeoTestCase {
	private static PlaneGeo geo;
	private static Divider div;
	private static final long maxSize = 126;
	private static final long avoidMargin = 6;
	private static final long cutSize = 114;
	private static final List<Long> cutSizes = new ArrayList<Long>();
	private static final List<Long> brCutSizes = new ArrayList<Long>();
	private static List<Point> avoidLocations = null;
	
	@BeforeClass
	public static void setUp() throws Exception {
		geo = new PlaneGeo();
		geo.setAngularTolerance(0.1);
		geo.setLinearTolerance(0.1);
		geo.setPointClass(DwgPoint.class);
		div = new Divider(geo);
		div.setAvoidMargin(avoidMargin);
		div.setMaxSize(maxSize);
		div.setThreadedCutSize(cutSize);
		cutSizes.add(126L);
		cutSizes.add(120L);
		brCutSizes.add(126L);
		div.setCutSizes(cutSizes);
		avoidLocations = new ArrayList<Point>();
	}
	@Test
	public void testSubdivide() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Point start = new DwgPoint(new double[] { 0.0, 0.0 });
		Point end1 = new DwgPoint(new double[] { 134.0, 0.0 });
		Point end2 = new DwgPoint(new double[] { 133.0, 0.0 });
		Point avoid = new DwgPoint(new double[] { 60.0, 0.0 });
		long startTakeout = 3;
		long endTakeout = 4;
		double cutTakeout = 0.0; // grooved case
		// 1. cut Pipe is 1" longer than max. (134-4-3-126)
		// First try cut is on 129(126+3) which is too close (5") from end.
		// Second cut at 123 should work
		// 2. cut Pipe is exactly the max: no cuts
		
		avoidLocations.clear();
		avoidLocations.add(avoid);
		
		boolean ret1 = div.subdivide(start, end1, startTakeout, endTakeout, cutTakeout, avoidLocations, cutSizes);
		Map<Point[], List<Point>> cutMap1 = div.getCutMap();
		
		Assert.assertTrue(ret1);
		Assert.assertEquals(1, cutMap1.size());
		for(Point[] a : cutMap1.keySet()){
			Assert.assertEquals(avoid, a[0]);
			Assert.assertEquals(end1, a[1]);
			Assert.assertEquals(1, cutMap1.get(a).size());
			Point cut = cutMap1.get(a).get(0);
			Assert.assertEquals(120 + startTakeout, Math.round(cut.x(0)));
		}

		boolean ret2 = div.subdivide(start, end2, startTakeout, endTakeout, cutTakeout, avoidLocations, cutSizes);
		Map<Point[], List<Point>> cutMap2 = div.getCutMap();
		Assert.assertTrue(ret2);
		Assert.assertEquals(0, cutMap2.size());
	}
	@Test
	public void testSubdivide2() {
		Point start = new DwgPoint(new double[] { 0.0, 0.0 });
		Point end = new DwgPoint(new double[] { 267.0, 0.0 });
		Point avoid1 = new DwgPoint(new double[] {200.0, 0.0 });
		Point avoid2 = new DwgPoint(new double[] {129.0, 0.0 });
		long startTakeout = 3;
		long endTakeout = 4;
		double cutTakeout = 0;
        // cut pipe is 8" longer than twice the max
		// 1. avoid is far between 1st and 2nd max-try cuts:
		//  expect 1 max cut at start-avoid and 1 max cut at avoid-end
		// 2. avoid is on the first max-try cut:
		//  expect the same except that first cut should be the second choice.
		
		for(Point avoid : new Point[] {avoid1, avoid2}){
			avoidLocations.clear();
			avoidLocations.add(avoid);
			boolean ret = div.subdivide(start, end, startTakeout, endTakeout, cutTakeout, avoidLocations, cutSizes);
			Map<Point[], List<Point>> cutMap = div.getCutMap();
			
			Assert.assertTrue(ret);
			Assert.assertEquals(2, cutMap.size());
			int i = 0;
			for(Point[] a : cutMap.keySet()){
				Point expectedKeyStart = (i == 0) ? start : avoid;
				Point expectedKeyEnd = (i == 0) ? avoid : end;
				long expectedX;
				if(avoid == avoid1){
					expectedX = startTakeout + 126 + i*126;
				}else{
					expectedX = startTakeout + 120 + i*126;
				}
				Assert.assertEquals(expectedKeyStart, a[0]);
				Assert.assertEquals(expectedKeyEnd, a[1]);
				Assert.assertEquals(1, cutMap.get(a).size());
				Point cut = cutMap.get(a).get(0);
				Assert.assertEquals(expectedX, Math.round(cut.x(0)));
				i++;
			}
		}
	}
	@Test
	public void testSubdivideThreaded(){
		Point start = new DwgPoint(new double[] { 1.0, 0.0 });
		Point end = new DwgPoint(new double[] { 128.0, 0.0 });
		
		div.subdivideThreaded(start, end);
		List<Point> cuts = div.getCutList();

		Assert.assertEquals(1, cuts.size());
		Point cut = cuts.get(0);
		Assert.assertEquals(Math.round(cutSize) + 1, Math.round(cut.x(0)));
	}

}
