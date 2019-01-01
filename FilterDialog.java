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

public class FilterDialog extends JDialog {

	/**
	 * Okno do filtrowania skryptów i przypisywania/wypisywania filtrów zaznaczonym skryptom
	 * przyjmuje wskaźnik na głowne okno by korzystać z zaznaczenia na Skryptach
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	public String filter;
	private JList<ListData> filtersList;
	private DefaultListModel<ListData> filterListModel;
	private void filtersListFill()
	{
		/**
		 * Wypełnij listę z filtrami odczytując z bazy
		 */
		((DefaultListModel<ListData>) filterListModel).clear(); //najpierw wyczyszczenie
		String sql ="Select id, nazwa from Filtr order by nazwa;";
		Kolektor.db.conn=null;
		((DefaultListModel<ListData>) filterListModel).clear();
    	try {Kolektor.db.conn = DriverManager.getConnection("jdbc:sqlite:"+Kolektor.db.url);
        Statement stmt  = Kolektor.db.conn.createStatement();
   	ResultSet rs = stmt.executeQuery(sql);
   	//dodawanie kolejnych elementów do listy
			while (rs.next()) {
				((DefaultListModel<ListData>) filterListModel).addElement(new ListData(rs.getInt("id"),rs.getString("nazwa")));
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
	public FilterDialog(Kolektor mainWindow) {
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
			filtersList = new JList<ListData>();
			filtersList.setLayoutOrientation(JList.VERTICAL_WRAP); //jaśli się mieszczą to kilka kolumn
			filtersList.setBorder(new LineBorder(new Color(0, 0, 0), 1, true)); //obramowanie dla ozdoby
			   filterListModel = new DefaultListModel<ListData>();
			   JScrollPane scrollPanel = new JScrollPane();
			   scrollPanel.setBounds(0, 0, 177, 169);
			   contentPanel.add(scrollPanel);
			   filtersList.setModel(filterListModel);
			   scrollPanel.add(filtersList);
			   scrollPanel.setViewportView(filtersList);
			   filtersListFill(); //wstępne wypełnienie
		}
		{
			//dodanie nowego filtru do listy
			JButton btnAdd = new JButton("Dodaj");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//Okno pytające o nazwę
					String dialogResult=JOptionPane.showInputDialog(null, "Nazwa", "", JOptionPane.OK_CANCEL_OPTION);
					if(dialogResult!="") //tekst nie jest pusty tylko jak coś wpisze i zatwierdzi
					{
						//dodanie do bazy
						Kolektor.db.insert("Filtr", new String[] {"Nazwa"},new String[] {dialogResult});
						filtersListFill(); //ponowne wypełnieni listy
					}
				}
			});
			btnAdd.setBounds(189, 12, 81, 25);
			contentPanel.add(btnAdd);
		}
		{
			JButton btnRemove = new JButton("Usuń");
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//usunięcie skryptu
					int dialogResult=JOptionPane.showConfirmDialog(null, "Czy usunąć zaznaczone Filtry wraz z przypisaniami do Skryptów?","Usunięcie Filtrów", JOptionPane.OK_CANCEL_OPTION);
					 if (dialogResult==0) // zatwierdzono
					 {
						 List<ListData> rows= filtersList.getSelectedValuesList(); //lista zaznaczonych pozycji
							for(ListData d: rows)
							{
								//usunięcie wszystkich (nie usuwam powiązań, bo to załatwia constraint "ON DELETE CASCADE")
								Kolektor.db.delete("Filtr", "id="+d.getId()); // tabela, warunek w where
							};
							filtersListFill(); // ponowne wypełnieni listy
					 }
				}
			});
			btnRemove.setBounds(189, 38, 81, 25);
			contentPanel.add(btnRemove);
		}
		JButton btnAddToSelected = new JButton("Przypisz");
		btnAddToSelected.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 10)); // odróżniam inną czcionką
		btnAddToSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Analogiczna funkcja jak kawałek wyżej, tylko dodająca powiązanie filtru ze skryptami
				int dialogResult=JOptionPane.showConfirmDialog(null, "Czy przypisać zaznaczone Filtry do zaznaczonych Skryptów?","Przypisane Filtry", JOptionPane.OK_CANCEL_OPTION);
				 if (dialogResult==0)
				 {
				 List<ListData> rows= filtersList.getSelectedValuesList();
					for(ListData d: rows) // pętla po filtrach
					{
						for(int row : mainWindow.skryptTable.getSelectedRows()) //pętla po zaznaczonych skryptach
						{
							Kolektor.db.insert("SkryptFiltr", new String[]{"skrypt_id","filtr_id"},
									new String[] {mainWindow.skryptTableModel.getValueAt(row, 0).toString(),""+d.getId()});
						}
					};
					filtersListFill(); // ponowne wypełnieni listy
				 }
			}
		});
		btnAddToSelected.setBounds(189, 103, 97, 33);
		contentPanel.add(btnAddToSelected);
		
		
		JButton btnRemoveSelection = new JButton("Odznacz");
		btnRemoveSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filtersList.clearSelection(); //wyczyszczenie zaznacznia
			}
		});
		btnRemoveSelection.setFont(new Font("Dialog", Font.BOLD, 10)); // inna czcionka
		btnRemoveSelection.setBounds(189, 67, 97, 33);
		contentPanel.add(btnRemoveSelection);
		{
			JButton btnRemoveFromSelected = new JButton("Wypisz");
			btnRemoveFromSelected.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int dialogResult=JOptionPane.showConfirmDialog(null, "Czy usunąć zaznaczone Filtry z zaznaczonych Skryptów?","Usunięcie Filtrów", JOptionPane.OK_CANCEL_OPTION);
					 if (dialogResult==0) // zatwierdzono
					 {
					 List<ListData> rows= filtersList.getSelectedValuesList();
						for(ListData d: rows) //pętla po filtrach
						{
							for(int row : mainWindow.skryptTable.getSelectedRows()) // pętla po skryptach
							{
								//usunięcie powiązań
								Kolektor.db.delete("SkryptFiltr", "skrypt_id="+mainWindow.skryptTableModel.getValueAt(row, 0)+" and filtr_id="+d.getId());
							}
						};
					//tu nie trzeba odświerzać, bo lista się nie zmieni
					 }
				}
			});
			btnRemoveFromSelected.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 10)); // inna czcionka
			btnRemoveFromSelected.setBounds(189, 139, 97, 33);
			contentPanel.add(btnRemoveFromSelected);
		}
		{
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						filter="";
						String id= new String();
						//pętla tworząca listę id zaznaczonych filtrów
						for(ListData i : filtersList.getSelectedValuesList()) 
						{
							id=id+i.getId()+',';
						}
						if (id.length()>0)
						{
							id=id.substring(0, id.length()-1); // usunięcie nadmiarowego przecinka
							// warunek do przekazania do okna głównego
							 filter="exists (select 1 from SkryptFiltr where skrypt_id=s.id and filtr_id in ("+id+"))";
						}
						//hide();
						setVisible(false); // ukrycie okna (bez zbędnego kombinowania mam zmienną z filtrem i zachowuje zaznaczenia w liście)
						mainWindow.skryptListFill(); // odświerzenie listy na oknie głównym
						
				}
				});

				//okButton.setActionCommand("OK");
				buttonPanel.add(okButton);
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
				buttonPanel.add(cancelButton);
			}
		}
	}
}
