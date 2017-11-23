/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.domain;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;

import javax.persistence.*;

import java.io.Serializable;
import java.util.List;

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
	
	public abstract List<MaterialReserved> getMaterialReservedList();

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
