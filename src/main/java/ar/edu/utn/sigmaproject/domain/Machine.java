package ar.edu.utn.sigmaproject.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Indexed
@Analyzer(definition = "edge_ngram")
public class Machine implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Transient
	final Logger logger = LoggerFactory.getLogger(Machine.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@DocumentId
	Long id;

	@ManyToOne
	MachineType machineType;

	@Field
	String name = "";

	@Field
	Integer year = Calendar.getInstance().get(Calendar.YEAR);

	@Transient
	Duration usedTime;

	@Column
	String usedTimeInternal;

	public Machine() {

	}

	public Machine(MachineType machineType, String name, Integer year, Duration usedTime) {
		this.machineType = machineType;
		this.name = name;
		this.year = year;
		this.setUsedTime(usedTime);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MachineType getMachineType() {
		return machineType;
	}

	public void setMachineType(MachineType machineType) {
		this.machineType = machineType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Duration getUsedTime() {
		if (this.usedTime == null && this.usedTimeInternal != null) {
			try {
				this.usedTime = DatatypeFactory.newInstance().newDuration(this.usedTimeInternal);
			} catch (Exception e) {
				logger.error("Error while trying to deserialize Duration representation(" + this.usedTimeInternal + "): " + e.toString());
			}
		}
		return usedTime;
	}

	public void setUsedTime(Duration usedTime) {
		this.usedTime = usedTime;
		if (usedTime != null) {
			this.usedTimeInternal = usedTime.toString();
		} else {
			this.usedTimeInternal = null;
		}
	}
}
