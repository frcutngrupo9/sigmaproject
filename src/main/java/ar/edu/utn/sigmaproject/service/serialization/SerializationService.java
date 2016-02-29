package ar.edu.utn.sigmaproject.service.serialization;

import java.io.*;
import java.util.List;

/**
 *
 * @author DanielRH
 */
public class SerializationService {
	private String pathArchivo;

	public SerializationService(String nombreArchivo)
	{
		pathArchivo = "src/main/java/ar/edu/utn/sigmaproject/persistence/" + nombreArchivo + ".drh";
	}

	@SuppressWarnings("rawtypes")
	public List obtenerLista()
	{
		List datos = null;
		try
		{
			File archivo = new File(pathArchivo);
			if(archivo.exists() == true) {
				FileInputStream fileIn = new FileInputStream(pathArchivo);
				ObjectInputStream entrada = new ObjectInputStream(fileIn);
				datos = (List)entrada.readObject();
				entrada.close();
				fileIn.close();
			}
		}catch(Exception e){System.out.println("Error en Obtener Lista en SerializationService: " + e.toString());}
		return datos;
	}

	@SuppressWarnings("rawtypes")
	public void grabarLista(List lista)
	{
		try
		{
			FileOutputStream fileOut=new FileOutputStream(pathArchivo);
			ObjectOutputStream salida=new ObjectOutputStream(fileOut);
			salida.writeObject(lista);
			salida.close();
			fileOut.close();
		}catch(Exception e){System.out.println("Error en Grabar Lista en SerializationService: "+e.toString());}
	}
}
