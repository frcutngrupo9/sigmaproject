package ar.edu.utn.sigmaproject.domain.indexbridge;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

public class RawMaterialTypeFieldsClassBridge implements FieldBridge {

	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		RawMaterialType rawMaterialType = (RawMaterialType)value;
		String fieldValue = rawMaterialType.getLength().doubleValue() + "x" +
				rawMaterialType.getDepth().doubleValue() + "x" +
				rawMaterialType.getWidth().doubleValue();

		Field field = new StringField(
				name,
				fieldValue,
				luceneOptions.getStore());
		field.setBoost(luceneOptions.getBoost());
		document.add(field);
	}

}
