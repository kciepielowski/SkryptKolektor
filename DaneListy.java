package SkryptKolektor;
import java.util.Arrays;


public class DaneListy {
	/**
	 * prosta pomocnicza klasa do przechowywania jednocześnie id i nazwy w listach
	 * zawiera dodatkową funkcję CzyObraz() by nie musiać ponownie sprawdzać w bazie
	 */
	    private int id;
	    private String name;
	    
	    DaneListy(int id, String name) {
	        this.id = id;
	        this.name = name;
	    }

	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    @Override
	    public String toString() {
	        return name;
	    }
	    public boolean CzyObraz() {
	    	/**
	    	 * funkcja do sprawdza po rozszerzeniu czy plik jest obrazem
	    	 * jako obraz traktuje: "jpg","jpeg","png","gif","bmp","tiff","ico"
	    	 */
	    	String ext= "";

	    	int i = name.lastIndexOf('.'); // położenie ostatniej '.' w nazwie 
	    	if (i > 0) { // jeśli jest '.'
	    	    ext = name.substring(i+1).toLowerCase(); //cały tekst po kropce w małych literach
	    	}
	    	String[] eobraz = {"jpg","jpeg","png","gif","bmp","tiff","ico"}; //lista rozszerzeń traktowanych jako obraz
	    	return Arrays.stream(eobraz).anyMatch(ext::equals);	// zwróć wynik porównania
	    }
}
