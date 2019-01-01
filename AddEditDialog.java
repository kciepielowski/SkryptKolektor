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

public class AddEditDialog extends JDialog {

	/**
	 * Okno dodawania skryptów
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField nameField;
	private JTextPane descriptionField;
	public boolean check, aTeF;
	public String name,description;
	public int id;
	private JButton filesButton; 
	private JList<ListData> filtersList;
	private DefaultListModel<ListData> filtersListModel;

public void setParams(int i, String n,String o, boolean b)
{
	/**
	 * Ustaw id=1,nazwa=n,opis=o, true dla dodania, false dla zmiany
	 */
	id=i;
	name=n;
	description=o;
	aTeF=b; //dodanie true, zmiana false
	nameField.setText(name);
	descriptionField.setText(description);
	filesButton.setEnabled(!aTeF);
	filtersListFill();
}
private void filtersListFill()
{
	/**
	 * wypełnienie pola z zaznaczonymi filtrami
	 */
	((DefaultListModel<ListData>) filtersListModel).clear();
	String sql ="Select f.id, f.nazwa from Filtr f, SkryptFiltr fs where f.id=fs.filtr_id and fs.skrypt_id="+id+" order by f.nazwa;";
	Kolektor.db.conn=null;
	((DefaultListModel<ListData>) filtersListModel).clear();
	try {Kolektor.db.conn = DriverManager.getConnection("jdbc:sqlite:"+Kolektor.db.url);
    Statement stmt  = Kolektor.db.conn.createStatement();
	ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			((DefaultListModel<ListData>) filtersListModel).addElement(new ListData(rs.getInt("id"),rs.getString("nazwa")));
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

	public AddEditDialog() {
		/**
		 * Okno Dodaj, albo zmień w zależności od wywołania funkcji ustaw
		 */
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			nameField = new JTextField();
			nameField.setToolTipText("Nazwa");
			nameField.setColumns(10);
		}
		
		JLabel nameLabel = new JLabel("Nazwa");
		
		JLabel descriptionLabel = new JLabel("Opis");
		
		descriptionField = new JTextPane();
		descriptionField.setToolTipText("opis");
			nameField.setText(name);
			descriptionField.setText(description);
		
		filesButton = new JButton("Dodaj pliki");
		filesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// wywołanie okna wyboru plików
				JFileChooser fileChooser = new JFileChooser();
				// na przyszłość jakbym chciał dodać filtr rozszerzeń plików
				//FileNameExtensionFilter filter = new FileNameExtensionFilter(
				//        "JPG & GIF Images", "jpg", "gif");
				//    chooser.setFileFilter(filter);
				fileChooser.setMultiSelectionEnabled(true); // wiele zaznaczeń
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // tylko pliki
				    int returnVal = fileChooser.showOpenDialog(getParent()); //wywołaj
				    if(returnVal == JFileChooser.APPROVE_OPTION) { //zatwierdzono
				    File[] files =fileChooser.getSelectedFiles(); // tablica z zaznaczonymi plikami
				    for (File file: files) // pętla po plikach
				    {
				    	try {
							Kolektor.db.insertFile(id, file.getName(),file); // wczytanie do bazy
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
						.addComponent(nameLabel)
						.addComponent(descriptionLabel))
					.addGap(24)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(filesButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane)
							.addContainerGap())
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(nameField, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(descriptionField, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
									.addGap(6)))
							.addGap(21))))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(nameLabel))
					.addGap(6)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(descriptionLabel)
						.addComponent(descriptionField, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(filesButton)
							.addGap(83))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
							.addContainerGap())))
		);
		
		filtersList = new JList<ListData>();
		filtersListModel = new DefaultListModel<ListData>();
		filtersList.setModel(filtersListModel);
		filtersList.setEnabled(false);
		scrollPane.setViewportView(filtersList);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//po zatwierdzeniu zapisz zmienne i ukryj okno
						check=true;
						name=nameField.getText();
						description=descriptionField.getText();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPanel.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// jeśli odrzucono schowaj okno
						check=false;
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPanel.add(cancelButton);
			}
		}
	}
}
