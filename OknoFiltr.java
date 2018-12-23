package SkryptKolektor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JScrollPane;
import java.awt.Font;

public class OknoFiltr extends JDialog {

	/**
	 * Okno do filtrowania skryptów i przypisywania/wypisywania filtrów zaznaczonym skryptom
	 * przyjmuje wskaźnik na głowne okno by korzystać z zaznaczenia na Skryptach
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	public String Filtr;
	private JList<DaneListy> list;
	private DefaultListModel<DaneListy> listModel;
	private void wypelnij()
	{
		/**
		 * Wypełnij listę z filtrami odczytując z bazy
		 */
		((DefaultListModel<DaneListy>) listModel).clear(); //najpierw wyczyszczenie
		String sql ="Select id, nazwa from Filtr order by nazwa;";
		Kolektor.db.conn=null;
		((DefaultListModel<DaneListy>) listModel).clear();
    	try {Kolektor.db.conn = DriverManager.getConnection("jdbc:sqlite:"+Kolektor.db.url);
        Statement stmt  = Kolektor.db.conn.createStatement();
   	ResultSet rs = stmt.executeQuery(sql);
   	//dodawanie kolejnych elementów do listy
			while (rs.next()) {
				((DefaultListModel<DaneListy>) listModel).addElement(new DaneListy(rs.getInt("id"),rs.getString("nazwa")));
				//rs.getBinaryStream("");
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
		}		}
	public OknoFiltr(Kolektor k) {
		/**
		 * Zawsze wywoływać OknoFiltr(this);
		 * pozwala to widzieć zaznaczone Skrypty
		 */
		//zdefiniowanie wyglądu okna
		setBounds(100, 100, 300, 241);
		setResizable(false); //to okno jest na sztywno
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			list = new JList<DaneListy>();
			list.setLayoutOrientation(JList.VERTICAL_WRAP); //jaśli się mieszczą to kilka kolumn
			list.setBorder(new LineBorder(new Color(0, 0, 0), 1, true)); //obramowanie dla ozdoby
			   listModel = new DefaultListModel<DaneListy>();
			   JScrollPane scrollPane = new JScrollPane();
			   scrollPane.setBounds(0, 0, 177, 169);
			   contentPanel.add(scrollPane);
			   list.setModel(listModel);
			   scrollPane.add(list);
			   scrollPane.setViewportView(list);
			   wypelnij(); //wstępne wypełnienie
		}
		{
			//dodanie nowego filtru do listy
			JButton btnNewButton = new JButton("Dodaj");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//Okno pytające o nazwę
					String wynik=JOptionPane.showInputDialog(null, "Nazwa", "", JOptionPane.OK_CANCEL_OPTION);
					if(wynik!="") //tekst nie jest pusty tylko jak coś wpisze i zatwierdzi
					{
						//dodanie do bazy
						Kolektor.db.insert("Filtr", new String[] {"Nazwa"},new String[] {wynik});
						wypelnij(); //ponowne wypełnieni listy
					}
				}
			});
			btnNewButton.setBounds(189, 12, 81, 25);
			contentPanel.add(btnNewButton);
		}
		{
			JButton btnUsu = new JButton("Usuń");
			btnUsu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//usunięcie skryptu
					int wynik=JOptionPane.showConfirmDialog(null, "Czy usunąć zaznaczone Filtry wraz z przypisaniami do Skryptów?","Usunięcie Filtrów", JOptionPane.OK_CANCEL_OPTION);
					 if (wynik==0) // zatwierdzono
					 {
						 List<DaneListy> rows= list.getSelectedValuesList(); //lista zaznaczonych pozycji
							for(DaneListy d: rows)
							{
								//usunięcie wszystkich (nie usuwam powiązań, bo to załatwia constraint "ON DELETE CASCADE")
								Kolektor.db.delete("Filtr", "id="+d.getId()); // tabela, warunek w where
							};
							wypelnij(); // ponowne wypełnieni listy
					 }
				}
			});
			btnUsu.setBounds(189, 38, 81, 25);
			contentPanel.add(btnUsu);
		}
		JButton btnDodajZaznaczonym = new JButton("Przypisz");
		btnDodajZaznaczonym.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 10)); // odróżniam inną czcionką
		btnDodajZaznaczonym.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Analogiczna funkcja jak kawałek wyżej, tylko dodająca powiązanie filtru ze skryptami
				int wynik=JOptionPane.showConfirmDialog(null, "Czy przypisać zaznaczone Filtry do zaznaczonych Skryptów?","Przypisane Filtry", JOptionPane.OK_CANCEL_OPTION);
				 if (wynik==0)
				 {
				 List<DaneListy> rows= list.getSelectedValuesList();
					for(DaneListy d: rows) // pętla po filtrach
					{
						for(int row : k.table.getSelectedRows()) //pętla po zaznaczonych skryptach
						{
							Kolektor.db.insert("SkryptFiltr", new String[]{"skrypt_id","filtr_id"}, new String[] {k.tm.getValueAt(row, 0).toString(),""+d.getId()});
						}
					};
					wypelnij(); // ponowne wypełnieni listy
				 }
			}
		});
		btnDodajZaznaczonym.setBounds(189, 103, 97, 33);
		contentPanel.add(btnDodajZaznaczonym);
		
		
		JButton btnUsuSelekcje = new JButton("Odznacz");
		btnUsuSelekcje.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				list.clearSelection(); //wyczyszczenie zaznacznia
			}
		});
		btnUsuSelekcje.setFont(new Font("Dialog", Font.BOLD, 10)); // inna czcionka
		btnUsuSelekcje.setBounds(189, 67, 97, 33);
		contentPanel.add(btnUsuSelekcje);
		{
			JButton btnWypisz = new JButton("Wypisz");
			btnWypisz.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int wynik=JOptionPane.showConfirmDialog(null, "Czy usunąć zaznaczone Filtry z zaznaczonych Skryptów?","Usunięcie Filtrów", JOptionPane.OK_CANCEL_OPTION);
					 if (wynik==0) // zatwierdzono
					 {
					 List<DaneListy> rows= list.getSelectedValuesList();
						for(DaneListy d: rows) //pętla po filtrach
						{
							for(int row : k.table.getSelectedRows()) // pętla po skryptach
							{
								//usunięcie powiązań
								Kolektor.db.delete("SkryptFiltr", "skrypt_id="+k.tm.getValueAt(row, 0)+" and filtr_id="+d.getId());
							}
						};
					//tu nie trzeba odświerzać, bo lista się nie zmieni
					 }
				}
			});
			btnWypisz.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 10)); // inna czcionka
			btnWypisz.setBounds(189, 139, 97, 33);
			contentPanel.add(btnWypisz);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Filtr="";
						String id= new String();
						//pętla tworząca listę id zaznaczonych filtrów
						for(DaneListy i : list.getSelectedValuesList()) 
						{
							id=id+i.getId()+',';
						}
						if (id.length()>0)
						{
							id=id.substring(0, id.length()-1); // usunięcie nadmiarowego przecinka
							// warunek do przekazania do okna głównego
							 Filtr="exists (select 1 from SkryptFiltr where skrypt_id=s.id and filtr_id in ("+id+"))";
						}
						//hide();
						setVisible(false); // ukrycie okna (bez zbędnego kombinowania mam zmienną z filtrem i zachowuje zaznaczenia w liście)
						k.wypelnij(); // odświerzenie listy na oknie głównym
						
				}
				});

				//okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Anuluj");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						// nic nie rób jak anulowano, tylko ukryj okno
						setVisible(false);
					}
				});
				//cancelButton.setActionCommand("Anuluj");
				buttonPane.add(cancelButton);
			}
		}
	}
}
