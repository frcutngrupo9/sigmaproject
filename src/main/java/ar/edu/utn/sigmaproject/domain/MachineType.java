package ar.edu.utn.sigmaproject.domain;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

@Entity
@Indexed
@Analyzer(definition = "edge_ngram")
public class MachineType implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Transient
	final Logger logger = LoggerFactory.getLogger(MachineType.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Field
	String name = "";

	@Field
	String details = "";

	@Transient
	Duration deteriorationTime;

	@Column
	String deteriorationTimeInternal;

	public MachineType() {

	}

	public MachineType(String name, String details, Duration deteriorationTime) {
		this.name = name;
		this.details = details;
		this.setDeteriorationTime(deteriorationTime);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Duration getDeteriorationTime() {
		if (this.deteriorationTime == null && this.deteriorationTimeInternal != null) {
			try {
				this.deteriorationTime = DatatypeFactory.newInstance().newDuration(this.deteriorationTimeInternal);
			} catch (Exception e) {
				logger.error("Error while trying to deserialize Duration representation(" + this.deteriorationTimeInternal + "): " + e.toString());
			}
		}
		return deteriorationTime;
	}

	public void setDeteriorationTime(Duration deteriorationTime) {
		this.deteriorationTime = deteriorationTime;
		if (deteriorationTime != null) {
			this.deteriorationTimeInternal = deteriorationTime.toString();
		} else {
			this.deteriorationTimeInternal = null;
		}
	}
}
