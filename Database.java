package SkryptKolektor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
/**
 * Klasa obsługująca bazę danych
 */
	public String url; // ścieżka do pliku
	public Connection conn; //zmienna z połączeniem
	public static String convert(String[] name, String ap) {
		/**
		 * Pomocnicza funkcja zmieniająca tablicę stringów na jeden tekst
		 * name - tablica
		 * ap - roździelacz tekstu
		 */
	    StringBuilder sb = new StringBuilder();
	    for (String st : name) { 
	        sb.append(st).append(ap);
	    }
	    if (name.length != 0) sb.deleteCharAt(sb.length()-ap.length());
	    return sb.toString();
	}
	public static String stringRepeat(int count, String str) {
		/**
		 * Pomocnicza funkcja powtarzająca tekst po ',' określoną ilość razy
		 * napisana do uzyzkania czegoś takiego: "?,?,?,?" dla inserta
		 */
	    StringBuilder sb = new StringBuilder();
	    for (int i=0; i<count; i++) { 
	        sb.append(str).append(',');
	    }
	    if (count != 0) sb.deleteCharAt(sb.length()-1);
	    return sb.toString();
	}
        public void createTables() {
        	/**
        	 * Stworzenie struktury tabel
        	 */
        	conn=null;
        	String sql [] = {"CREATE TABLE IF NOT EXISTS Skrypt (\n"
                    + "	id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,\n"
                    + "	nazwa TEXT,\n"
                    + "	opis TEXT\n"
                    + ");",
                    "CREATE TABLE IF NOT EXISTS Plik (\n"
                    + "	id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,\n"
                    + " skrypt_id INTEGER NOT NULL,\n"
                    + "	nazwa TEXT,\n"
                    + "	plik BLOB,\n"
                    + " CONSTRAINT fk_Skrypt\n"
                    + " FOREIGN KEY (skrypt_id)\n"
                    + " REFERENCES Skrypt(id)\n"
                    + " ON DELETE CASCADE\n"
                    + ");",
                    "CREATE TABLE IF NOT EXISTS Filtr (\n"
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,\n"
                    + " nazwa TEXT NOT NULL\n"
                    + ");",
                    "CREATE TABLE IF NOT EXISTS SkryptFiltr (\n"
                    +" skrypt_id INTEGER NOT NULL,\n"
                    +" filtr_id INTEGER NOT NULL,\n"
                    + " CONSTRAINT fk_Skrypt\n"
                    + " FOREIGN KEY (skrypt_id)\n"
                    + " REFERENCES Skrypt(id)\n"
                    + " ON DELETE CASCADE,\n"
                    + " CONSTRAINT fk_Filtr\n"
                    + " FOREIGN KEY (filtr_id)\n"
                    + " REFERENCES Filtr(id)\n"
                    + " ON DELETE CASCADE\n"
                    + ");",
                    "create unique index if not exists U_SkryptFiltr on SkryptFiltr ( skrypt_id, filtr_id );",
                    "create unique index if not exists U_Filtr on Filtr ( nazwa) ;"};
            try {conn = DriverManager.getConnection("jdbc:sqlite:"+url);
                    Statement stmt = conn.createStatement();
                    for (String s: sql)
                    {
                stmt.execute(s);
                    }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        public void insert(String table,String[] col, Object[] val) {
        	/**
        	 * Insert do wskazanej tabeli, pól, podanych danych (tylko String i double)
        	 */
            String sql = "INSERT OR IGNORE INTO "+table+"("+convert(col,",")+") VALUES("+stringRepeat(col.length,"?")+")";
            conn=null;
            try {
            		conn = DriverManager.getConnection("jdbc:sqlite:"+url);
            		conn.createStatement().executeUpdate("PRAGMA foreign_keys=ON");
                    PreparedStatement pstmt = conn.prepareStatement(sql);
            	int i=0;
            	for (Object st : val) { 
            		i++;
            		if(st.getClass().getName()=="java.lang.String"){ // jeśli klasą jest String
            			pstmt.setString(i, st.toString());
            		}else
            		{
            			pstmt.setDouble(i, (double)st); // w przeciwnym wypadku traktuj jako liczbę
            		}
        	    }
                pstmt.executeUpdate(); // odpal
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        public void update(String table,String[] col, Object[] val, String where) {
        	/**
        	 * Update wskazanej tabeli, pól o zadane wartości, przy podanym warunku
        	 */
            String sql = "UPDATE "+table+" set "+convert(col,"=?,")+" where "+where;
            conn=null;
            System.out.println(sql);
            try {
            		conn = DriverManager.getConnection("jdbc:sqlite:"+url);
            		conn.createStatement().executeUpdate("PRAGMA foreign_keys=ON");
                    PreparedStatement pstmt = conn.prepareStatement(sql);
            	int i=0;
            	for (Object st : val) { 
            		i++;
            		if(st.getClass().getName()=="java.lang.String"){ // jeśli string
            			pstmt.setString(i, st.toString());
            		}else
            		{
            			pstmt.setDouble(i, (double)st); // innaczej traktu jako liczbę
            		}
        	    }
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        public void delete(String table,String where) {
        	/**
        	 * Delete ze wskazanej tabeli przy wskazanym warunku
        	 */
            String sql = "DELETE FROM "+table+" where "+where+";";
            conn=null;
            try {
            		conn = DriverManager.getConnection("jdbc:sqlite:"+url);
            		conn.createStatement().executeUpdate("PRAGMA foreign_keys=ON");
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        public void insertFile(int id, String name, File file) throws FileNotFoundException {
            /**
             * Funkcja wczytująca wskazany plik dla skryptu o podanym id
             */
            String sql = "insert into Plik (skrypt_id,nazwa,plik) values (?,?,?); ";
            conn=null;
            try {conn = DriverManager.getConnection("jdbc:sqlite:"+url);
                    PreparedStatement pstmt = conn.prepareStatement(sql);
            	pstmt.setInt(1, id);
            	pstmt.setString(2, name);
                	pstmt.setBinaryStream(3, new FileInputStream(file),(int) file.length());
                pstmt.executeUpdate();
     
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        public long getNextID(String tableName) {
        	/**
        	 * Pobranie następnego id z tabeli sequencer-ów, lub 1 gdy pusta
        	 */
            String sql="SELECT seq from sqlite_sequence where lower(name)=lower('"+tableName+"');";
            long autoIncrement =0;
        	conn=null;
        	try {conn = DriverManager.getConnection("jdbc:sqlite:"+url);
            Statement stmt  = conn.createStatement();
       	ResultSet rs = stmt.executeQuery(sql);
    			while (rs.next()) {
                    autoIncrement = rs.getLong("seq");
    			}
    		} catch (SQLException e) {
    			System.out.println(e.getMessage());
    		}finally {
                try {
                    if( conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
    		}
            return autoIncrement + 1;
        }
}