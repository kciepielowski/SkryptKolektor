package SkryptKolektor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JList;

public class OknoDodaj extends JDialog {

	/**
	 * Okno dodawania skryptów
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtNazwa;
	private JTextPane txtpnOpis;
	public boolean zatw, dtzf;
	public String Nazwa,Opis;
	public int id;
	private JButton btnPliki; 
	private JList<DaneListy> list;
	private DefaultListModel<DaneListy> listModel;

public void ustaw(int i, String n,String o, boolean b)
{
	/**
	 * Ustaw id=1,nazwa=n,opis=o, true dla dodania, false dla zmiany
	 */
	id=i;
	Nazwa=n;
	Opis=o;
	dtzf=b; //dodanie true, zmiana false
	txtNazwa.setText(Nazwa);
	txtpnOpis.setText(Opis);
	btnPliki.setEnabled(!dtzf);
	wypelnij();
}
private void wypelnij()
{
	/**
	 * wypełnienie pola z zaznaczonymi filtrami
	 */
	((DefaultListModel<DaneListy>) listModel).clear();
	String sql ="Select f.id, f.nazwa from Filtr f, SkryptFiltr fs where f.id=fs.filtr_id and fs.skrypt_id="+id+" order by f.nazwa;";
	Kolektor.db.conn=null;
	((DefaultListModel<DaneListy>) listModel).clear();
	try {Kolektor.db.conn = DriverManager.getConnection("jdbc:sqlite:"+Kolektor.db.url);
    Statement stmt  = Kolektor.db.conn.createStatement();
	ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			((DefaultListModel<DaneListy>) listModel).addElement(new DaneListy(rs.getInt("id"),rs.getString("nazwa")));
		}
	} catch (SQLException e) {
		System.out.println(e.getMessage());
	}finally {
        try {
            if (Kolektor.db.conn != null) {
            	Kolektor.db.conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
	}
	}

	public OknoDodaj() {
		/**
		 * Okno Dodaj, albo zmień w zależności od wywołania funkcji ustaw
		 */
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			txtNazwa = new JTextField();
			txtNazwa.setToolTipText("Nazwa");
			txtNazwa.setColumns(10);
		}
		
		JLabel lblNazwa = new JLabel("Nazwa");
		
		JLabel lblOpis = new JLabel("Opis");
		
		txtpnOpis = new JTextPane();
		txtpnOpis.setToolTipText("opis");
			txtNazwa.setText(Nazwa);
			txtpnOpis.setText(Opis);
		
		btnPliki = new JButton("Dodaj pliki");
		btnPliki.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// wywołanie okna wyboru plików
				JFileChooser JFC = new JFileChooser();
				// na przyszłość jakbym chciał dodać filtr rozszerzeń plików
				//FileNameExtensionFilter filter = new FileNameExtensionFilter(
				//        "JPG & GIF Images", "jpg", "gif");
				//    chooser.setFileFilter(filter);
				JFC.setMultiSelectionEnabled(true); // wiele zaznaczeń
				JFC.setFileSelectionMode(JFileChooser.FILES_ONLY); // tylko pliki
				    int returnVal = JFC.showOpenDialog(getParent()); //wywołaj
				    if(returnVal == JFileChooser.APPROVE_OPTION) { //zatwierdzono
				    File[] fl =JFC.getSelectedFiles(); // tablica z zaznaczonymi plikami
				    for (File f: fl) // pętla po plikach
				    {
				    	try {
							Kolektor.db.insertFile(id, f.getName(),f); // wczytanie do bazy
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
				    }
				    }
			}
		});
		// takie tam ustawienie położenia komponentów
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNazwa)
						.addComponent(lblOpis))
					.addGap(24)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(btnPliki)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane)
							.addContainerGap())
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(txtNazwa, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(txtpnOpis, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
									.addGap(6)))
							.addGap(21))))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtNazwa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNazwa))
					.addGap(6)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblOpis)
						.addComponent(txtpnOpis, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(btnPliki)
							.addGap(83))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
							.addContainerGap())))
		);
		
		list = new JList<DaneListy>();
		listModel = new DefaultListModel<DaneListy>();
		list.setModel(listModel);
		list.setEnabled(false);
		scrollPane.setViewportView(list);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//po zatwierdzeniu zapisz zmienne i ukryj okno
						zatw=true;
						Nazwa=txtNazwa.getText();
						Opis=txtpnOpis.getText();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// jeśli odrzucono schowaj okno
						zatw=false;
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
