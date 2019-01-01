package SkryptKolektor;

import java.awt.Container;
import java.awt.Image;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GraphicsPreviewDialog extends JFrame {

	private static final long serialVersionUID = 1L; 

public static int id; //id pliku z brazem
private Image image; // sam obraz jako ikona do podstawienia w labelkę
	public GraphicsPreviewDialog(int id) {
		/**
		 * odczyt pliku z obrazem
		 */
		String sql ="Select plik from Plik s where id="+id+";";
		Kolektor.db.conn=null;
		try {
			Kolektor.db.conn = DriverManager.getConnection("jdbc:sqlite:"+Kolektor.db.url);
	    Statement stmt = null;
			stmt = Kolektor.db.conn.createStatement();
		ResultSet rs = null;
		rs = stmt.executeQuery(sql);
					while (rs.next()) {
						// tworzę nowy obraz jako ikonę (ImageIcon) przekształcając strumień binnarny (getBinaryStream) na zbuforowany obraz (ImageIO.read)
				        image= ImageIO.read(rs.getBinaryStream("plik"));
					}
	} catch (IOException |SQLException e) {
				System.out.println(e.getMessage());
	}finally { // zamknięcie połączenie po wszystkim
				try {
				    if (Kolektor.db.conn != null) {
				        Kolektor.db.conn.close();
				    }
				} catch (SQLException ex) {
				    System.out.println(ex.getMessage());
				}
	};
	//labelka do której podstawiam obraz jako ikonę
	//reso
	JLabel label=new JLabel(new ImageIcon(image));
        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        
        pane.setLayout(gl);
        //utworzenie odstępów po bokach okna
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(label)
        );

        gl.setVerticalGroup(gl.createParallelGroup()
                .addComponent(label)
        );

        pack();// ustawia rozmiar okna do rozmiaru komponentów

        setTitle("Podgląd");
        // dzięki temu otwiera na środku, innaczej w lewym górnym rogu
        setLocationRelativeTo(null);
        }
	};
