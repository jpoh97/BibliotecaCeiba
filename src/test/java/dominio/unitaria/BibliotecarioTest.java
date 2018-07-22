package dominio.unitaria;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import testdatabuilder.LibroTestDataBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BibliotecarioTest {

	@Test
	public void esPrestadoTest() {

		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();

		Libro libro = libroTestDataBuilder.build();

		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);

		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(libro);

		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);

		// act 
		boolean esPrestado = bibliotecario.esPrestado(libro.getIsbn());

		//assert
		assertTrue(esPrestado);
	}

	@Test
	public void libroNoPrestadoTest() {

		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();

		Libro libro = libroTestDataBuilder.build();

		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);

		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(null);

		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);

		// act 
		boolean esPrestado = bibliotecario.esPrestado(libro.getIsbn());

		//assert
		assertFalse(esPrestado);
	}

	@Test
	public void esPalindromoTest() {
		//Arrange
		String isbnTest = "A12BC3DD3CB21A";
		Bibliotecario bibliotecario = new Bibliotecario(null, null);

		//Act
		boolean esPalindromo = bibliotecario.esPalindromo(isbnTest);

		//Assert
		assertTrue(esPalindromo);
	}

	@Test
	public void noEsPalindromoTest() {
		//Arrange
		String isbnTest = "A12BC3DD3CB21AA";
		Bibliotecario bibliotecario = new Bibliotecario(null, null);

		//Act
		boolean esPalindromo = bibliotecario.esPalindromo(isbnTest);

		//Assert
		assertFalse(esPalindromo);
	}

	@Test
	public void esMayorQueTreintaTest() {
		//Arrange
		String isbnTest = "ASBC1234FGB234348";
		Bibliotecario bibliotecario = new Bibliotecario(null, null);

		//Act
		boolean esMayorQueTreinta = bibliotecario.esMayorATreinta(isbnTest);

		//Assert
		assertTrue(esMayorQueTreinta);
	}

	@Test
	public void noEsMayorQueTreintaTest() {
		// Es menor que 30
		//Arrange
		String isbnTest = "A1234FGB23434";
		Bibliotecario bibliotecario = new Bibliotecario(null, null);

		//Act
		boolean esMayorQueTreinta = bibliotecario.esMayorATreinta(isbnTest);

		//Assert
		assertFalse(esMayorQueTreinta);

		// Es igual a 30
		//Arrange
		isbnTest = "A9F99G3";

		//Act
		esMayorQueTreinta = bibliotecario.esMayorATreinta(isbnTest);

		//Assert
		assertFalse(esMayorQueTreinta);
	}

	@Test
	public void incrementarDosSemanasTest() throws ParseException {
		//Arrange
		DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
		Date fechaInicial = formatoFecha.parse("24-05-2017");
		Date fechaEsperada = formatoFecha.parse("09-06-2017");
		Bibliotecario bibliotecario = new Bibliotecario(null, null);

		//Act
		Date fechaObtenida = bibliotecario.incrementarDosSemanas(fechaInicial);

		//Assert
		assertEquals(fechaEsperada, fechaObtenida);
	}

	@Test
	public void incrementarDosSemanasConTresDomingosTest() throws ParseException {
		//Arrange
		DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
		Date fechaInicial = formatoFecha.parse("26-05-2017");
		Date fechaEsperada = formatoFecha.parse("12-06-2017");
		Bibliotecario bibliotecario = new Bibliotecario(null, null);

		//Act
		Date fechaObtenida = bibliotecario.incrementarDosSemanas(fechaInicial);

		//Assert
		assertEquals(fechaEsperada, fechaObtenida);
	}

	@Test
	public void incrementarDiasTest() throws ParseException {
		//Arrange
		DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
		Date fechaInicial = formatoFecha.parse("26-05-2017");
		Date fechaEsperada = formatoFecha.parse("29-05-2017");
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(fechaInicial);
		Bibliotecario bibliotecario = new Bibliotecario(null, null);

		//Act
		Date fechaObtenida = bibliotecario.incrementarDias(calendario, 3);

		//Assert
		assertEquals(fechaEsperada, fechaObtenida);
	}
}
