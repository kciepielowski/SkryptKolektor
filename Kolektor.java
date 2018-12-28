package SkryptKolektor;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.nio.file.StandardCopyOption;

public class Kolektor implements KeyListener{
	/**
	 * Główna klasa aplikacji
	 */
	private JFrame frmSkryptKolektor;
	private JTextField txtSzukaj;
	public JTable table;
	public DefaultTableModel tm;
	private OknoFiltr par; // okno z filtrem
	private OknoDodaj szcz; // okno Dodaj/Zmień (Szczegóły)
	public static Baza db;
	private String war; // warunek do wyszukiwania skryptów
	private ListModel<DaneListy> listModel;
	private JList<DaneListy> list;
	
	//start programu
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Kolektor window = new Kolektor(); //konstruktor
					window.frmSkryptKolektor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Konstruktor aplikacji
	 */
	public Kolektor() {
		db = new Baza(); // nowa baza
		db.url="dane.db"; // nazwa pliku z bazą
		db.createTables(); // utworzenie struktury bazy (jeśli nie istnieje)
		System.out.println("Aktualny folder = " +
	              System.getProperty("user.dir")); // informacyjnie zwrócenie na konsolę gdzie trzma bazę danych
		initialize(); // reszta tworzenia programu w osobnej funkcji
	}
	
    public void wypelnij() {
    	/**
    	 * wypełnieni tablicy skryptami
    	 */
    	String sql ="Select id, nazwa, opis from Skrypt s";
    	//sprawdzenie warunku i filtrów
    	if(war!="")
    		{
    		sql+=" where "+war;
    		if(par.Filtr!="")sql+=" and "+par.Filtr;
    		}
    	else if(par.Filtr!="")sql+=" where "+par.Filtr;
    	// sortuj po nazwie
    	sql+=" order by nazwa, id;";
    	db.conn=null;
    	for (int i=tm.getRowCount()-1; i>=0;i--)tm.removeRow(i); //wyczyszczenie tablicy
    	try {db.conn = DriverManager.getConnection("jdbc:sqlite:"+db.url);
        Statement stmt  = db.conn.createStatement();
   	ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) { // pętla wypełniająca
				tm.addRow(new Object[]{rs.getInt("id"),rs.getString("nazwa"),rs.getString("opis")});
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}finally {
            try {
                if (db.conn != null) {
                    db.conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
		}
    }
    public void wypliste()
    {
    	/**
    	 * Wypełnienie listy plików z zaznaczonego skryptu
    	 */
		((DefaultListModel<DaneListy>) listModel).clear();
		if(table.getSelectedRow()!=-1)
		{
		String sql ="Select id, nazwa from Plik s where skrypt_id="+tm.getValueAt(table.getSelectedRow(),0)+" order by nazwa;";
		db.conn=null;
		((DefaultListModel<DaneListy>) listModel).clear();
    	try {db.conn = DriverManager.getConnection("jdbc:sqlite:"+db.url);
        Statement stmt  = db.conn.createStatement();
   	ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) { // pętla wypełniająca
				((DefaultListModel<DaneListy>) listModel).addElement(new DaneListy(rs.getInt("id"),rs.getString("nazwa")));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}finally {
            try {
                if (db.conn != null) {
                    db.conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
		}}

    }
	private void initialize() {
		/**
		 * Reszta tworzenia programu
		 */
		frmSkryptKolektor = new JFrame();
		frmSkryptKolektor.setTitle("Skrypt Kolektor");
		frmSkryptKolektor.setBounds(100, 100, 800, 600);
		frmSkryptKolektor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		par =new OknoFiltr(this);//przygotuj okno z filtrem
		par.Filtr="";// pusty filtr na starcie
		war="";// pusty warunek na starcie
		szcz = new OknoDodaj();// przygotuj okno dodaj/zmień
		txtSzukaj = new JTextField();
		txtSzukaj.setText("");
		txtSzukaj.setToolTipText("Wyszukiwanie po opisie i nazwie");
		JButton btnFiltruj = new JButton("Filtruj");
		btnFiltruj.setIcon(new ImageIcon(Kolektor.class.getResource("/com/sun/java/swing/plaf/windows/icons/DetailsView.gif")));
		JScrollPane scrollPane = new JScrollPane();
		JButton btnDodaj = new JButton("Dodaj");
		btnDodaj.setIcon(new ImageIcon(Kolektor.class.getResource("/com/sun/java/swing/plaf/windows/icons/File.gif")));
		JButton btnZmien = new JButton("Zmień");
		btnZmien.setIcon(new ImageIcon(Kolektor.class.getResource("/javax/swing/plaf/metal/icons/ocean/menu.gif")));
		JButton btnUsun = new JButton("Usuń");
		btnUsun.setIcon(new ImageIcon(Kolektor.class.getResource("/javax/swing/plaf/metal/icons/ocean/close.gif")));
		JButton btnExport = new JButton("Export");
		btnExport.setIcon(new ImageIcon(Kolektor.class.getResource("/javax/swing/plaf/metal/icons/ocean/upFolder.gif")));
		JButton btnImport = new JButton("Import");
		btnImport.setIcon(new ImageIcon(Kolektor.class.getResource("/javax/swing/plaf/metal/icons/ocean/hardDrive.gif")));
		// definicja komponentu z tablicą
		table = new JTable(); 
		tm = new DefaultTableModel(
				null,
			new String[] {
				"ID", "Nazwa", "Opis"
			}
		) {
			private static final long serialVersionUID = 1L;
			// nadpisanie funkcji sprawdzającej możliwość edycji
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false; // wyłączenie edycji dla wszystkich komórek
		    }
		};
		table.setModel( tm);
		//ukrycie kolumny 0 z id poprzez zerowy rozmiar
		table.getColumnModel().getColumn(0).setMinWidth(0);
		   table.getColumnModel().getColumn(0).setMaxWidth(0);
		   table.getColumnModel().getColumn(0).setWidth(0);
		   table.addKeyListener(this); //tablica nasłuchuje wciśnięć klawiszy
		   wypelnij();// wstępne wypałnienie
		   
		 // Definicja komponentu z listą plików
		   list = new JList<DaneListy>();
		   list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		   list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		   list.setVisibleRowCount(-1);
		   listModel = new DefaultListModel<DaneListy>();
		   list.setModel(listModel);
		   list.addKeyListener(this);
		   ListSelectionModel sM = table.getSelectionModel();
		//rozmieszczenie komponentów
				GroupLayout groupLayout = new GroupLayout(frmSkryptKolektor.getContentPane());
				groupLayout.setHorizontalGroup(
					groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(2)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(txtSzukaj, GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
									.addGap(2)))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(2)
									.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(btnFiltruj, GroupLayout.DEFAULT_SIZE, 150, 150)
										.addComponent(btnDodaj, GroupLayout.DEFAULT_SIZE, 150, 150)
										.addComponent(btnZmien, GroupLayout.DEFAULT_SIZE, 150, 150)
										.addComponent(btnUsun, GroupLayout.DEFAULT_SIZE, 150, 150)
										.addComponent(btnImport, GroupLayout.DEFAULT_SIZE, 150, 150)
										.addComponent(btnExport, GroupLayout.DEFAULT_SIZE, 150, 150)))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(2)
									.addComponent(list, GroupLayout.DEFAULT_SIZE, 150, 150)))
							.addGap(2))
				);
				groupLayout.setVerticalGroup(
					groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtSzukaj, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnFiltruj))
							.addGap(2)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnDodaj)
									.addGap(3)
									.addComponent(btnZmien)
									.addGap(3)
									.addComponent(btnUsun)
									.addGap(3)
									.addComponent(btnImport)
									.addGap(3)
									.addComponent(btnExport)
									.addGap(7)
									.addComponent(list, GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
									.addGap(4)))
							.addContainerGap())
				);
				
		// obsługa zdarzeń na przyszłość można przerzucić do osobnych funkcji
				
		txtSzukaj.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){ // wciśnięto enter w polu szukaj, zapisanie warunku
					if(txtSzukaj.getText().length()>1) //pole nie jest puste
						war="(lower(nazwa) like lower('%"+txtSzukaj.getText()+"%') or lower(opis) like lower('%"+txtSzukaj.getText()+"%')"+
					"or exists (select 1 from Plik where skrypt_id=s.id and "+              //szukanie w pliku tylko do 1Mb
								"(lower(nazwa) like lower('%"+txtSzukaj.getText()+"%') or ( length(plik)<1048576  AND lower(plik) like lower('%"+txtSzukaj.getText()+"%') ))))";
					else war="";
					((DefaultListModel<DaneListy>) listModel).clear();
					wypelnij();// wypełnienie ponowne listy skryptów
				}
			}
		});
		btnFiltruj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				par.setVisible(true);
			}
		});
		
		/*	obsługa zdarzeń ukrycia/pokazania okna Dodaj/Zmień
		 *  mogłem tylko przekazać wskaźnik do okna i wykonać poniższe bezpośrednio w tamtej klasie,
		 *  ale zrobiłem innaczej w ramach testów */
		
		szcz.addComponentListener(new ComponentListener() {
			  public void componentHidden(ComponentEvent e)
			  {// ukryto okno dodaj/zmień wykonaj zapytania
					if(szcz.zatw) {
						if(szcz.dtzf)
						{
						db.insert("Skrypt", new String[] {"Nazwa","Opis"}, new Object[] {szcz.Nazwa,szcz.Opis});
						}else
						{
							db.update("Skrypt", new String[] {"Nazwa","Opis"}, new Object[] {szcz.Nazwa,szcz.Opis},"id="+szcz.id);		
						}
						wypelnij();// ponowie wypełnij tablicę
					}
			  }

			  public void componentShown(ComponentEvent e)
			  {
				  // przy ponownym pokazaniu wyczyść stan przycisku zatwierdź
				  szcz.zatw=false;
			  }
			  // nie potrzebuję poniższych, ale muszą być
			  public void componentMoved(ComponentEvent e){}
			  public void componentResized(ComponentEvent e){}
			  //------------------------------
			  
			});
		
		btnDodaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Wywołanie okna dodaj
				szcz.setVisible(true);
				szcz.ustaw(0, "", "", true);
			}
		});
		
		btnZmien.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//jeśli coś zaznaczone wywołanie okna zmień
				int r=table.getSelectedRow();
				if(r>-1)
				{
				szcz.setVisible(true);
				szcz.ustaw((int) tm.getValueAt(r, 0), (String) tm.getValueAt(r, 1), (String) tm.getValueAt(r, 2), false);
				}
			}
		});
		btnUsun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//usuń zaznaczone skrypty
				int wynik=JOptionPane.showConfirmDialog(null, "Czy usunąć zaznaczone skrypty?","Usunięcie skryptów", JOptionPane.OK_CANCEL_OPTION);
				 if (wynik==0)
				 {
				int [] rows= table.getSelectedRows();
				for(int d: rows)
				{
					db.delete("Skrypt", "id="+tm.getValueAt(d, 0));
				};
				wypelnij(); // ponowne wypełnienie
				 }
			}
		});

		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//
				// import z plików
				//
				JFileChooser JFC = new JFileChooser();
				// na przyszłość wybór rozszerzenia
				//FileNameExtensionFilter filter = new FileNameExtensionFilter(
				//        "JPG & GIF Images", "jpg", "gif");
				//    chooser.setFileFilter(filter);
				JFC.setMultiSelectionEnabled(true);
				JFC.setFileSelectionMode(JFileChooser.FILES_ONLY); // tylko pliki
				    int returnVal = JFC.showOpenDialog(JFC);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				    File[] fl =JFC.getSelectedFiles();
				    for (File f: fl) // pętla po plikach robiąca insert do bazy
				    {
				    	try {
				    		long id=db.getNextID("skrypt");
				    		db.insert("Skrypt",new String[] {"nazwa"}, new Object[] {f.getName()});
							Kolektor.db.insertFile((int)id, f.getName(),f);
						} catch (FileNotFoundException el) {
							el.printStackTrace();
						}
				    }
				    wypelnij(); // ponownie wypełnij
				    }
			}
		});
					btnExport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// obsługa eksportu do folderu
					JFileChooser JFC = new JFileChooser();
					JFC.setMultiSelectionEnabled(false);
					JFC.setDialogType(JFileChooser.SAVE_DIALOG);
					JFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // tylko katalogi
					JFC.setDialogTitle("Wyeksportuj do");
					    int returnVal = JFC.showOpenDialog(JFC);
					    if(returnVal == JFileChooser.APPROVE_OPTION) {
					    	
					    	int [] rows= table.getSelectedRows();
							for(int d: rows)
							{
					    	String sql ="Select nazwa, plik from Plik s where Skrypt_id="+tm.getValueAt(d,0)+";";
							Kolektor.db.conn=null;
					    	try {Kolektor.db.conn = DriverManager.getConnection("jdbc:sqlite:"+Kolektor.db.url);
					        Statement stmt  = Kolektor.db.conn.createStatement();
					   	ResultSet rs = stmt.executeQuery(sql);
					   	char s;
								while (rs.next()) {
									
									if (System.getProperty("os.name").toLowerCase().indexOf("win")>=0) {
										s='\\';}else {s='/';}
																			
									File tF = new File(JFC.getSelectedFile().getPath()+s+rs.getString("nazwa"));
									java.nio.file.Files.copy(
											rs.getBinaryStream("plik"), 
										      tF.toPath(),StandardCopyOption.REPLACE_EXISTING);
								}
								}catch (SQLException e1) {
								System.out.println(e1.getMessage());
							} catch (IOException e1) {
									e1.printStackTrace();
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
					    }
			   }
			   }
			   );
		   sM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				//zmiana pozycji w menu, ponowne wypełnienie lisy plików
				wypliste();
			}
		   });
		   list.addMouseListener(new MouseAdapter() {
			    public void mouseClicked(MouseEvent evt) {
			        JList<?> list = (JList<?>)evt.getSource();
			        if (evt.getClickCount() == 2) { 
			        	//wywołanie okna podglądu przy podwójnym kliknięciu
			        	System.out.println(((DaneListy)list.getSelectedValue()).getId()+": "+list.getSelectedValue());
			        	if((((DaneListy) list.getSelectedValue())).CzyObraz()) // jeśli obraz podgląd obrazu
			        	{
			        	 OknoObraz ob = new OknoObraz(((DaneListy)list.getSelectedValue()).getId());
			        	 ob.setVisible(true);
			        	}
			        	else // w przeciwnym wypadku tekstowy
			        	{
			        	OknoPodglad p =new OknoPodglad();
			        	p.setVisible(true);
			        	p.wczytaj(((DaneListy)list.getSelectedValue()).getId());
			        	};
			        } 
			    }
			});
		   
		scrollPane.setViewportView(table);
		frmSkryptKolektor.getContentPane().setLayout(groupLayout);
	}

	

	@Override
	public void keyReleased(KeyEvent ke) {

		if (ke.getKeyCode()==127) // klawisz del
		{
			if (list.getSelectedValue()!=null) //cokolwiek zaznaczono w liście plików
			{
			 int wynik=JOptionPane.showConfirmDialog(null, "Czy usunąć plik " + list.getSelectedValue()+"?","Usunięcie pliku", JOptionPane.OK_CANCEL_OPTION);
			 if (wynik==0) // zatwierdzono
			 {
				 // usuń i ponownie wypełnij
				 db.delete("Plik", "id="+((DaneListy)list.getSelectedValue()).getId()); 
				 wypliste();
			 }
			}else // wypełnij
			{
				int wynik=JOptionPane.showConfirmDialog(null, "Czy usunąć zaznaczone skrypty?","Usunięcie skryptów", JOptionPane.OK_CANCEL_OPTION);
				 if (wynik==0) // zatwierdzono
				 {
					 int [] rows= table.getSelectedRows();
						for(int d: rows)
						{
							// usuń
							db.delete("Skrypt", "id="+tm.getValueAt(d, 0));
						};
						wypelnij(); // ponownie wypełnij
				 }
			}
		}
	}
	// tych nie potrzebuję, ale muszą być
	public void keyTyped(KeyEvent ke) {}
	public void keyPressed(KeyEvent arg0) {}
	// --------------------------------------
}
