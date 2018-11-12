package org.autopipes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import org.autopipes.model.AreaCutSheet.BranchInfo;
import org.autopipes.model.AreaCutSheet.CutSheetInfo;
import org.autopipes.model.AreaCutSheet.EdgeMultiplicity;
import org.autopipes.model.DrawingLayer.Designation;

/**
 * Jaxb bean which contains information about an area within a drawing.
 * It contains searchable attributes, area-level configuration XML
 * (areaOptions property of type AreaOptions) and the actual raw Acad drawing
 * (areaBody of type AreaBody). The rendering property contains a collection
 * of DwgEntity objects passed back to AutoCAD for decoration. This property is ignored
 * by SQL operations and is injected when returning rendering information.
 */
// JAXB
//@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "area-root")
// JPA
@Entity
@Table(name="floor_area_jpa", uniqueConstraints={@UniqueConstraint(columnNames = {"drawing_id", "area_name"})})
public class DrawingArea {
	private static Logger logger = Logger.getLogger(DrawingArea.class);
	private static final int CLOB_MAX = 100000;
//	
// Inner
//
	/**
	 * Area-level properties stored in the Acad block attached to the area.
	 */
	public enum AreaTag{
		areaId("area-id"),
		areaName("area-name"),
		mainStartNo("main-start-no"),
		branchStartNo("branch-start-no"),
		mainLabel("main-label"),
		branchLabel("branch-label"),
		shortLimit("ignore-shorter-than"),
		cutLimit("cut-longer-than"),
		cutSizeB("cut-branch-into"),
		cutSizeM("cut-main-into"),
		cutClearance("main-cut-clearance"),
		takeoutRounding("takeout-rounding");

		private final String tag;
		AreaTag(final String tag){
			this.tag = tag;
		}
		public String getTag() {
			return tag;
		}
	}

	@XmlRootElement(name = "readiness")
	public enum Readiness{
    	Ready,
    	Empty,
    	Disconnected,
    	LoopInBranch,
    	MainFittingFailure,
    	FittingFailure,
    	NoRaiser,
    	BadLengthOptionFormat,
    	NoClearanceToCutMain,
    	NotReady;
    }
	
	@Embeddable
	public static final class Key implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@Column(name="drawing_id")
		Long drawingId;
		
		@Column(name="area_id")
		Long areaId;
		
		public Key(){
		}
		
		public Key(Long drawingId, Long areaId){
			this.drawingId = drawingId;
			this.areaId = areaId;
		}

		public Long getDrawingId() {
			return drawingId;
		}

		public Long getAreaId() {
			return areaId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((areaId == null) ? 0 : areaId.hashCode());
			result = prime * result + ((drawingId == null) ? 0 : drawingId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (areaId == null) {
				if (other.areaId != null)
					return false;
			} else if (!areaId.equals(other.areaId))
				return false;
			if (drawingId == null) {
				if (other.drawingId != null)
					return false;
			} else if (!drawingId.equals(other.drawingId))
				return false;
			return true;
		}
		
	}
//
// Main
//
	@EmbeddedId
	private Key key;
	
	@Column(name = "area_name")
    protected String areaName;
    
	@Column(name = "area_readiness")
	@Enumerated(EnumType.STRING)
	protected Readiness areaReadiness;
	
	@Column(name = "defect_count")
    protected int defectCount;
	
    @Lob
	@Column(name = "options", length = CLOB_MAX)
    protected String areaOptionsXml;
    @Transient
    protected AreaOptions areaOptions;
    
    @Lob
	@Column(name = "area", length = CLOB_MAX)
    protected String areaBodyXml;
    @Transient
    protected AreaBody areaBody;
    
    @Lob
	@Column(name = "cut_sheet", length = CLOB_MAX)
    protected String areaCutSheetXml;
    @Transient
    protected AreaCutSheet areaCutSheet;
    
    @Transient
    protected List<DwgEntity> rendering;
	
	/**
	 * Adds cut-sheet info records to the branch map. 
	 */
    public void countEdgeMultiplicity(){
      logger.info("+countEdgeMultiplicity");
	  Map<Integer, Boolean> visitedMap = new HashMap<Integer, Boolean>();
	  Map<CutSheetInfo, EdgeMultiplicity> branchEdgeMap
	    = new HashMap<CutSheetInfo, EdgeMultiplicity>();
	  for(Pipe e : getAreaBody().getEdgesInOrder()){
   		if(e.getDesignation() != Designation.Branch){
   			continue; // skip main and heads
   		}
   		if(e.isIgnored()){
   			continue; // skip short branches
   		}
   		int brId = e.getId();

   		// logic to skip duplicate branch ids
		Boolean visited = visitedMap.get(brId);
		if(getAreaBody().isOnMain(getAreaBody().getStartFitting(e))){
			// a branch root
   			if(visited == null){
   				// 1st time a branch-id encountered
   				// visitedMap is initialized to false to allow non-roots on this branch
   				visited = false;
   				visitedMap.put(brId, false);
   				// initialize the set
   				branchEdgeMap.clear();
   			}else if(!visited){
   				// we already seen a root with this branch id - skip all
   				visited = true;
   				visitedMap.put(brId, true);
   			}
   		}
   		if(visited != null && visited){
   			continue;
   		}

   		BranchInfo bInfo = getAreaCutSheet().getBranchMap().get(e.getId());
   		CutSheetInfo cInfo = cutSheetInfoForPipe(e);
  		if(getAreaBody().isOnMain(getAreaBody().getStartFitting(e))){
   			bInfo.setOrigin(e);
   		}
    		
   		EdgeMultiplicity em = branchEdgeMap.get(cInfo);
   		if(em == null){
   			em = new EdgeMultiplicity();
   			em.setCount(1);
   			em.setEdgeInfo(cInfo);
   			branchEdgeMap.put(cInfo, em);
   	        bInfo.getEdgeMultiplicity().add(em);
   		}else{
	   		em.setCount(em.getCount() + 1);
   		}
   	  }
      logger.info("-countEdgeMultiplicity");
    }
    /**
     * Creates CutSheetInfo object from a pipe
     * @param p the pipe
     * @return the info
     */
    public CutSheetInfo cutSheetInfoForPipe(Pipe p){
   		CutSheetInfo cInfo = new CutSheetInfo();
   		cInfo.setPipe(p);
   		PipeFitting end = getAreaBody().getEndFitting(p);
   		cInfo.setEndFitting(end.getFitting());
		PipeFitting start = getAreaBody().getStartFitting(p);
  		cInfo.setStartFitting(start.getFitting());
  		return cInfo;
    }
    
	public Long getDrawingId() {
		return key == null ? null : key.getDrawingId();
	}
	@XmlElement(name = "dwg-id")
	public void setDrawingId(final Long drawingId) {
		if(key == null) {
			key = new Key();
		}
		this.key.drawingId = drawingId;
	}
	
	public Long getAreaId() {
		return key == null ? null : key.getAreaId();
	}
	@XmlElement(name = "area-id")
	public void setAreaId(final Long areaId) {
		if(key == null) {
			key = new Key();
		}
		this.key.areaId = areaId;
	}
	public String getAreaName() {
		return areaName;
	}
    @XmlElement(name = "area-name", required = true)
	public void setAreaName(final String areaName) {
		this.areaName = areaName;
	}
	public AreaBody getAreaBody() {
		return areaBody;
	}
    @XmlElement(name = "area-body")
	public void setAreaBody(final AreaBody areaBody) {
		this.areaBody = areaBody;
	}
	public List<DwgEntity> getRendering() {
    	if(rendering == null){
    		rendering = new ArrayList<DwgEntity>();
    	}
		return rendering;
	}
	@XmlElementWrapper(name = "rendering")
    @XmlElement(name = "dwg-entity")
	public void setRendering(final List<DwgEntity> rendering) {
		this.rendering = rendering;
	}
	public Readiness getAreaReadiness() {
		return areaReadiness;
	}
    @XmlElement(name = "area-readiness", required = true)
	public void setAreaReadiness(final Readiness areaReadiness) {
		this.areaReadiness = areaReadiness;
	}
	public int getDefectCount() {
		return defectCount;
	}
    @XmlElement(name = "defect-count", required = true)
	public void setDefectCount(final int defectCount) {
		this.defectCount = defectCount;
	}
	public AreaOptions getAreaOptions() {
		return areaOptions;
	}
    @XmlElement(name = "area-options")
	public void setAreaOptions(final AreaOptions areaOptions) {
		this.areaOptions = areaOptions;
	}
    public AreaCutSheet getAreaCutSheet() {
    	if(areaCutSheet == null){
    		areaCutSheet = new AreaCutSheet();
    	}
		return areaCutSheet;
	}
    @XmlElement(name = "area-cut-sheet")
	public void setAreaCutSheet(AreaCutSheet areaCutSheet) {
		this.areaCutSheet = areaCutSheet;
	}
	public String getAreaOptionsXml() {
		return areaOptionsXml;
	}
	@XmlTransient
	public void setAreaOptionsXml(String areaOptionsXml) {
		this.areaOptionsXml = areaOptionsXml;
	}
	public String getAreaBodyXml() {
		return areaBodyXml;
	}
	@XmlTransient
	public void setAreaBodyXml(String areaBodyXml) {
		this.areaBodyXml = areaBodyXml;
	}
	public String getAreaCutSheetXml() {
		return areaCutSheetXml;
	}
	@XmlTransient
	public void setAreaCutSheetXml(String areaCutSheetXml) {
		this.areaCutSheetXml = areaCutSheetXml;
	}

}
