package ar.edu.utn.sigmaproject.domain;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;

@AnalyzerDefs(value = {
		@AnalyzerDef(name = "edge_ngram",
				tokenizer = @TokenizerDef(
						factory = StandardTokenizerFactory.class
				),
				filters = {
						@TokenFilterDef(factory = LowerCaseFilterFactory.class),
						@TokenFilterDef(
								factory = EdgeNGramFilterFactory.class,
								params = {
										@org.hibernate.search.annotations.Parameter(name = "maxGramSize", value = "25")
								}),
				}),
		@AnalyzerDef(name = "standard",
				tokenizer = @TokenizerDef(
						factory = StandardTokenizerFactory.class
				),
				filters = {
						@TokenFilterDef(factory = LowerCaseFilterFactory.class)
				})
})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public abstract String getDescription();

	@Override
	public String toString() {
		return getDescription();
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj != null && obj.hashCode() == this.hashCode());
	}
}
