package dominio;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;


public class Bibliotecario {

	// Mensajes de excepcion
	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	private static final String EL_LIBRO_SOLO_SE_PUEDE_UTILIZAR_EN_LA_BIBLIOTECA = "los libros palíndromos solo se " +
			"pueden utilizar en la biblioteca";

	private final int MAXIMA_SUMA_ISBN = 30;
	private final int DIAS_A_INCREMENTAR = 16;

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}

	public void prestar(String isbn, String usuarioQueRealizaElPrestamo) {
		Prestamo prestamo;
		Date fechaPrestamo;
		Date fechaEntregaMaxima = null;
		Libro libroAPrestar;

		// verificar si ya esta prestado
		if(esPrestado(isbn)) {
			throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		}
		// verificar palindromo
		if(esPalindromo(isbn)) {
			throw new PrestamoException(EL_LIBRO_SOLO_SE_PUEDE_UTILIZAR_EN_LA_BIBLIOTECA);
		}

		libroAPrestar = repositorioLibro.obtenerPorIsbn(isbn);
		fechaPrestamo = new Date();

		// verificar la suma del isbn
		if(esMayorATreinta(isbn)) {
			fechaEntregaMaxima = incrementarDosSemanas(fechaPrestamo);
		}

		prestamo = new Prestamo(fechaPrestamo, libroAPrestar, fechaEntregaMaxima, usuarioQueRealizaElPrestamo);
		repositorioPrestamo.agregar(prestamo);
	}

	public boolean esPrestado(String isbn) {
		if (Optional.ofNullable(isbn).isPresent()) {
			if (Optional.ofNullable(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn)).isPresent()) {
				return true;
			}
		}
		return false;
	}

	public boolean esPalindromo(String isbn) {
		if (Optional.ofNullable(isbn).isPresent()) {
			StringBuilder isbnReversado = new StringBuilder(isbn).reverse();
			return isbnReversado.toString().equals(isbn);
		}
		return false;
	}

	public boolean esMayorATreinta(String isbn) {
		if (!Optional.ofNullable(isbn).isPresent()) {
			return false;
		}
		String valoresNumericos = isbn.replaceAll("\\D+","");
		int suma = 0;
		for(char caracter : valoresNumericos.toCharArray()) {
			suma = suma + Character.getNumericValue(caracter);
		}
		return suma > MAXIMA_SUMA_ISBN;
	}

	public Date incrementarDosSemanas(Date date) {
		if (!Optional.ofNullable(date).isPresent()) {
			return null;
		}
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(date);
		if (calendario.get(Calendar.DAY_OF_WEEK) > Calendar.THURSDAY
				|| calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return incrementarDias(calendario, DIAS_A_INCREMENTAR + 1);
		}
		return incrementarDias(calendario, DIAS_A_INCREMENTAR);
	}

	public Date incrementarDias(Calendar calendario, int dias) {
		if (Optional.ofNullable(calendario).isPresent()) {
			calendario.add(Calendar.DATE, dias);
			return calendario.getTime();
		}
		return null;
	}
}
