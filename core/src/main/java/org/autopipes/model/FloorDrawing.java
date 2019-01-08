package org.autopipes.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;

	/**
	 * Bean which contains information which applies to the entire drawing.
	 * JAXB-annotated for passing messages between AuoCAD client and the Autopipes servlet.
	 * JPA-annotated for persistence in a database table but only some attributes
	 * may be queried individually - others are serialized into XML and saved in
	 * a single CLOB attribute (optionsRoot).
	 * The <code>area</code> is a SQL-transient attribute populated only in the
	 * reply message to the AutoCAD client. It contains status summaries for the areas.
	 */

// JAXB
//	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {})
	@XmlRootElement(name = "dwg-root")
// JPA	
	@Entity
	@Table(name="floor_drawing", uniqueConstraints={@UniqueConstraint(columnNames = {"name"})})
	public class FloorDrawing {
		private static final int CLOB_MAX = 10000;

		@Id
	    //http://www.oracle.com/technetwork/middleware/ias/id-generation-083058.html
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DWG_SEQ")
	    @SequenceGenerator(sequenceName = "floor_seq_jpa", initialValue = 1, allocationSize = 1, name = "DWG_SEQ")
		@Column(name = "id")
	    protected Long id;
		
		@Column(name = "name")
	    protected String dwgName;
		
		@Column(name = "text_size")
	    protected Double dwgTextSize;
		
		@Column(name = "upd_date")
	    protected Calendar dwgUpdateDate;
		
		// TODO: remove optionsRootXml and write a JPA type converter similar to one in templated version
	    @Lob
		@Column(name = "configuration", length = CLOB_MAX )
	    protected String optionsRootXml;	    
	    @Transient
	    protected DrawingOptions optionsRoot;

	    @Transient
	    protected Map<Long, DrawingArea> area;

	    // used only to define foreign key
		@OneToMany(fetch=FetchType.LAZY /*, cascade={CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval=true */)
		@JoinColumn(name="drawing_id", referencedColumnName="id")
		private List<DrawingArea> areas;

		// hide storage xml-string from clients
		@JsonIgnore
	    public String getOptionsRootXml() {
			return optionsRootXml;
		}
	    @XmlTransient
		public void setOptionsRootXml(String optionsRootXml) {
			this.optionsRootXml = optionsRootXml;
		}

		public String getDwgName() {
	        return dwgName;
	    }

	    @XmlElement(name = "dwg-name", required = true)
	    public void setDwgName(final String value) {
	        dwgName = value;
	    }

	    public Double getDwgTextSize() {
	        return dwgTextSize;
	    }

	    @XmlElement(name = "dwg-text-size")
	    public void setDwgTextSize(final Double value) {
	        dwgTextSize = value;
	    }

	    public Calendar getDwgUpdateDate() {
	        return dwgUpdateDate;
	    }
	    
	    @XmlElement(name = "dwg-update-date")
	    public void setDwgUpdateDate(final Calendar value) {
	        dwgUpdateDate = value;
	    }

		public Long getId() {
			return id;
		}

		@XmlElement(name = "dwg-id")
		public void setId(final Long id) {
			this.id = id;
		}

		public DrawingOptions getOptionsRoot() {
			return optionsRoot;
		}

	    @XmlElement(name = "options-root")
		public void setOptionsRoot(final DrawingOptions optionsRoot) {
			this.optionsRoot = optionsRoot;
		}

	    @XmlElement(name = "area")
		public Map<Long, DrawingArea> getArea() {
	        if (area == null) {
	        	area = new HashMap<Long, DrawingArea>();
	        }
			return area;
		}

		public void addArea(final DrawingArea area){
			getArea().put(area.getAreaId(), area);
		}
	}


