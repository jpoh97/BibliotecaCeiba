package dominio.integracion;

import static org.junit.Assert.fail;

import dominio.Prestamo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {

	private static final String CRONICA_DE_UNA_MUERTA_ANUNCIADA = "Cronica de una muerta anunciada";
	private static final String PRESTADOR = "Juan Pablo";
	private static final String ISBN_PALINDROMO = "A12B21A";
	private static final String ISBN_MAYOR_A_TREINTA = "A559188";
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioLibro repositorioLibros;
	private RepositorioPrestamo repositorioPrestamo;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioLibros = sistemaPersistencia.obtenerRepositorioLibros();
		repositorioPrestamo = sistemaPersistencia.obtenerRepositorioPrestamos();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void prestarLibroTest() {
		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		repositorioLibros.agregar(libro);
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		Prestamo prestamo;

		// act
		bibliotecario.prestar(libro.getIsbn(), PRESTADOR);

		// assert
		Assert.assertTrue(bibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));

		prestamo = repositorioPrestamo.obtener(libro.getIsbn());
		Assert.assertNull(prestamo.getFechaEntregaMaxima());
		Assert.assertEquals(CRONICA_DE_UNA_MUERTA_ANUNCIADA, prestamo.getLibro().getTitulo());
		Assert.assertEquals(PRESTADOR, prestamo.getNombreUsuario());
		Assert.assertNotNull(prestamo.getFechaSolicitud());
	}

	@Test
	public void prestarLibroNoDisponibleTest() {
		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		
		repositorioLibros.agregar(libro);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		bibliotecario.prestar(libro.getIsbn(), PRESTADOR);
		try {

			bibliotecario.prestar(libro.getIsbn(), PRESTADOR);
			fail();
			
		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE, e.getMessage());
		}
	}

	@Test
	public void prestarLibroConISBNPalindromoTest() {
		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).conIsbn(ISBN_PALINDROMO).build();

		repositorioLibros.agregar(libro);

		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		try {

			bibliotecario.prestar(libro.getIsbn(), PRESTADOR);
			fail();

		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.EL_LIBRO_SOLO_SE_PUEDE_UTILIZAR_EN_LA_BIBLIOTECA, e.getMessage());
		}
	}

	@Test
	public void prestarLibroConFechaDeEntregaTest() {
		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).conIsbn(ISBN_MAYOR_A_TREINTA).build();
		repositorioLibros.agregar(libro);
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		Prestamo prestamo;

		// act
		bibliotecario.prestar(libro.getIsbn(), PRESTADOR);
		prestamo = repositorioPrestamo.obtener(libro.getIsbn());

		// assert
		Assert.assertTrue(bibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(prestamo.getFechaEntregaMaxima());
		Assert.assertEquals(CRONICA_DE_UNA_MUERTA_ANUNCIADA, prestamo.getLibro().getTitulo());
		Assert.assertEquals(PRESTADOR, prestamo.getNombreUsuario());
		Assert.assertNotNull(prestamo.getFechaSolicitud());
	}
}
