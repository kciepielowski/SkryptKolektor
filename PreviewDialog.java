package SkryptKolektor;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class PreviewDialog extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JTextPane textPanel;
	
		public void wczytaj(int id) 
		{   
			/**
			 * odczyt pliku o podanym id
			 */
			String sql ="Select nazwa, plik from Plik s where id="+id+";";
			Kolektor.db.conn=null;
	    	try {Kolektor.db.conn = DriverManager.getConnection("jdbc:sqlite:"+Kolektor.db.url);
	        Statement stmt  = Kolektor.db.conn.createStatement();
	   	ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					//zmienna na cały tekst
					String text= new String();
					//jeśli odpalony na windowsie kodowanie CP1250, w przeciwnym razie UTF-8
					String codePage;
					if (System.getProperty("os.name").toLowerCase().indexOf("win")>=0) {
						codePage="CP1250";}else {codePage="UTF-8";}
					//użycie klasy scanner do wyciągnięcia tekstu z pliku.
					try(Scanner scanner = new Scanner(rs.getBinaryStream("plik"), codePage)) {
						//funkcje useDelimiter zawiera wyrażenia regularne jak dzielić, gdzie \\A to początek tekstu,
						//czyli w praktyce weźmie cały tekst z pliku
				        text = scanner.useDelimiter("\\A").next(); 
					textPanel.setText(text);
				}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}finally { //po wszystkim zamknij połączenie
	            try {
	                if (Kolektor.db.conn != null) {
	                    Kolektor.db.conn.close();
	                }
	            } catch (SQLException ex) {
	                System.out.println(ex.getMessage());
	            }
			}
			
	}
	public PreviewDialog() {
		/**
		 * utworzenie okna, właściwie wygenerowany kod z lekkimi poprawkami;
		 */
		setBounds(100, 100, 450, 300);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		
		JScrollPane scrollPanel = new JScrollPane();
		
		GroupLayout gl_contentPane = new GroupLayout(contentPanel);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(2)
					.addComponent(scrollPanel)
					.addGap(2))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
						)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(scrollPanel)
					)
		);
		
		textPanel = new JTextPane();
		//cały JScrollPane wypełnij polem tekstowym
		scrollPanel.setViewportView(textPanel);
		contentPanel.setLayout(gl_contentPane);
	}
}
