package by.gomelagro.incoming.gui.frames;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import by.gomelagro.incoming.base.ApplicationConstants;
import by.gomelagro.incoming.format.date.InvoiceDateFormat;
import by.gomelagro.incoming.gui.console.JConsole;
import by.gomelagro.incoming.gui.db.ConnectionDB;
import by.gomelagro.incoming.gui.db.WorkingIncomingTable;
import by.gomelagro.incoming.gui.db.files.WorkingFiles;
import by.gomelagro.incoming.gui.frames.enstatus.UpdateEnStatus;
import by.gomelagro.incoming.gui.frames.list.JMonthPanel;
import by.gomelagro.incoming.gui.frames.list.MonthPanelListModel;
import by.gomelagro.incoming.gui.frames.list.MonthYearItem;
import by.gomelagro.incoming.gui.frames.list.renderer.MonthPanelCellListRenderer;
import by.gomelagro.incoming.gui.progress.LoadFileProgressBar;
import by.gomelagro.incoming.properties.ApplicationProperties;
import by.gomelagro.incoming.service.EVatServiceSingleton;
import by.gomelagro.incoming.service.certificate.Certificate;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = 1L;

	private JTextPane console;
	private JMenuItem authMenuItem;
	private JMenuItem infoCertMenuItem;
	private JMenuItem connectMenuItem;
	private JMenuItem disconnectMenuItem;
	private JMenuItem loadFileMenuItem;
	private JMenuItem updateStatusMenuItem;
	private JMenuItem fastUpdateStatusMenuItem;
	private JMenuItem saveOneDayMenuItem;
	private JMenuItem saveBetweenMenuItem;
	
	private JLabel allInvoicesLabel;
	private JLabel completedLabel;
	private JLabel uncompletedLabel;
	private JLabel cancelledLabel;
	private JLabel undeterminedLabel;
	
	private JComboBox<String> yearComboBox;
	
	private MonthPanelListModel model;
	
	private final String title = ApplicationConstants.APP_MAINFRAME_TITLE+" "+ApplicationConstants.APP_VERSION;
	
	static{
		ApplicationProperties.getInstance();	
		System.setProperty("by.avest.loader.shared","true");
		System.setProperty("java.library.path",ApplicationProperties.getInstance().getLibraryPath().trim());
		System.setProperty("classpath", ApplicationProperties.getInstance().getClassPath().trim());
	}
	
	/**
	 * Create the application.
	 */
	public MainFrame() {
		if(WorkingFiles.isFile(ApplicationProperties.getInstance().getDbPath())){
			initialize();
			if(WorkingIncomingTable.Count.getCountAll() > 0)
				updateMainPanel(yearComboBox.getItemAt(yearComboBox.getSelectedIndex()));
			setVisible(true);
		}else{
			JOptionPane.showMessageDialog(null, "����������� ���� ���� ������."+System.lineSeparator()+
					"�������� ���� ���� ������.","��������",JOptionPane.WARNING_MESSAGE);
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("SQLite Databases (.sqlite)", "sqlite"));
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(chooser.showDialog(null,"��������� ����") == JFileChooser.APPROVE_OPTION){
				ApplicationProperties.getInstance().setDbPath(chooser.getSelectedFile().getAbsolutePath().trim());
				ApplicationProperties.getInstance().saveProperties();
				JOptionPane.showMessageDialog(null, "����� ���� � ����� ���� ������ ������� � ���������� ���������."+System.lineSeparator()+
						"����������, �������� ��������� ���������","����������",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {	
		try {
			ConnectionDB.getInstance().load();
		} catch (ClassNotFoundException | SQLException e) {
			JOptionPane.showMessageDialog(null,e.getLocalizedMessage(),"������",JOptionPane.ERROR_MESSAGE);
		}
		setTitle(title);
		setResizable(false);
		setBounds(100, 100, 920, 650);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{480, 129, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_mainPanel = new GridBagConstraints();
		gbc_mainPanel.insets = new Insets(0, 0, 5, 0);
		gbc_mainPanel.fill = GridBagConstraints.BOTH;
		gbc_mainPanel.gridx = 0;
		gbc_mainPanel.gridy = 0;
		getContentPane().add(mainPanel, gbc_mainPanel);
		GridBagLayout gbl_mainPanel = new GridBagLayout();
		gbl_mainPanel.columnWidths = new int[]{20, 20, 0, 60, 70, 500, 0};
		gbl_mainPanel.rowHeights = new int[]{20, 20, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 0};
		gbl_mainPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_mainPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		mainPanel.setLayout(gbl_mainPanel);
		
		JLabel lblyearLabel = new JLabel("���: ");
		lblyearLabel.setFont(new Font("Courier New", Font.BOLD, 11));
		GridBagConstraints gbc_lblyearLabel = new GridBagConstraints();
		gbc_lblyearLabel.anchor = GridBagConstraints.EAST;
		gbc_lblyearLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblyearLabel.gridx = 1;
		gbc_lblyearLabel.gridy = 1;
		mainPanel.add(lblyearLabel, gbc_lblyearLabel);
		
		yearComboBox = new JComboBox<String>();
		yearComboBox.setFont(new Font("Courier New", Font.BOLD, 12));
		yearComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if(evt.getStateChange() == ItemEvent.SELECTED){
					updateMainPanel(yearComboBox.getItemAt(yearComboBox.getSelectedIndex()));
				}
			}
		});
		if(WorkingIncomingTable.Count.getCountAll() > 0){
			if(fillYear()){
				yearComboBox.setSelectedIndex(0);
			}else{
				JOptionPane.showMessageDialog(null, "���������� ���������� �������������������� ������","������",JOptionPane.ERROR_MESSAGE);
			}
		}
		
		GridBagConstraints gbc_yearComboBox = new GridBagConstraints();
		gbc_yearComboBox.anchor = GridBagConstraints.SOUTH;
		gbc_yearComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_yearComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_yearComboBox.gridx = 2;
		gbc_yearComboBox.gridy = 1;
		mainPanel.add(yearComboBox, gbc_yearComboBox);
		
		model = new MonthPanelListModel();
		
		JList<JMonthPanel> vatList = new JList<JMonthPanel>();
		vatList.setValueIsAdjusting(true);
		vatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		vatList.setCellRenderer(new MonthPanelCellListRenderer());
		JScrollPane scroll_vatList = new JScrollPane(vatList);
		scroll_vatList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll_vatList	.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		vatList.setModel(model);
		vatList.setFont(new Font("Courier New", Font.PLAIN, 11));
		vatList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_vatList = new GridBagConstraints();
		gbc_vatList.gridheight = 13;
		gbc_vatList.fill = GridBagConstraints.BOTH;
		gbc_vatList.gridx = 5;
		gbc_vatList.gridy = 1;
		mainPanel.add(scroll_vatList, gbc_vatList);
		
		JLabel lblAllInvoicesLabel = new JLabel("����� ����: ");
		lblAllInvoicesLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
		GridBagConstraints gbc_lblAllInvoicesLabel = new GridBagConstraints();
		gbc_lblAllInvoicesLabel.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblAllInvoicesLabel.gridwidth = 2;
		gbc_lblAllInvoicesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblAllInvoicesLabel.gridx = 1;
		gbc_lblAllInvoicesLabel.gridy = 2;
		mainPanel.add(lblAllInvoicesLabel, gbc_lblAllInvoicesLabel);
		
		allInvoicesLabel = new JLabel("");
		allInvoicesLabel.setFont(new Font("Courier New", Font.BOLD, 11));
		GridBagConstraints gbc_allInvoicesLabel = new GridBagConstraints();
		gbc_allInvoicesLabel.anchor = GridBagConstraints.SOUTHEAST;
		gbc_allInvoicesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_allInvoicesLabel.gridx = 3;
		gbc_allInvoicesLabel.gridy = 2;
		mainPanel.add(allInvoicesLabel, gbc_allInvoicesLabel);
		
		JLabel ofThemLabel = new JLabel("�� ���: ");
		ofThemLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
		GridBagConstraints gbc_ofThemLabel = new GridBagConstraints();
		gbc_ofThemLabel.gridwidth = 2;
		gbc_ofThemLabel.anchor = GridBagConstraints.WEST;
		gbc_ofThemLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ofThemLabel.gridx = 1;
		gbc_ofThemLabel.gridy = 3;
		mainPanel.add(ofThemLabel, gbc_ofThemLabel);
		
		JLabel lblCompletedLabel = new JLabel("���������: ");
		lblCompletedLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
		GridBagConstraints gbc_lblCompletedLabel = new GridBagConstraints();
		gbc_lblCompletedLabel.anchor = GridBagConstraints.WEST;
		gbc_lblCompletedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblCompletedLabel.gridx = 2;
		gbc_lblCompletedLabel.gridy = 4;
		mainPanel.add(lblCompletedLabel, gbc_lblCompletedLabel);
		
		completedLabel = new JLabel("");
		completedLabel.setFont(new Font("Courier New", Font.BOLD, 11));
		GridBagConstraints gbc_completedLabel = new GridBagConstraints();
		gbc_completedLabel.anchor = GridBagConstraints.EAST;
		gbc_completedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_completedLabel.gridx = 3;
		gbc_completedLabel.gridy = 4;
		mainPanel.add(completedLabel, gbc_completedLabel);
		
		JLabel UncompletedLabel = new JLabel("�� ���������: ");
		UncompletedLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
		GridBagConstraints gbc_UncompletedLabel = new GridBagConstraints();
		gbc_UncompletedLabel.anchor = GridBagConstraints.WEST;
		gbc_UncompletedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_UncompletedLabel.gridx = 2;
		gbc_UncompletedLabel.gridy = 5;
		mainPanel.add(UncompletedLabel, gbc_UncompletedLabel);
		
		uncompletedLabel = new JLabel("");
		uncompletedLabel.setFont(new Font("Courier New", Font.BOLD, 11));
		GridBagConstraints gbc_uncompletedLabel = new GridBagConstraints();
		gbc_uncompletedLabel.anchor = GridBagConstraints.EAST;
		gbc_uncompletedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_uncompletedLabel.gridx = 3;
		gbc_uncompletedLabel.gridy = 5;
		mainPanel.add(uncompletedLabel, gbc_uncompletedLabel);
		
		JLabel lblCancelledLabel = new JLabel("������������: ");
		lblCancelledLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
		GridBagConstraints gbc_lblCancelledLabel = new GridBagConstraints();
		gbc_lblCancelledLabel.anchor = GridBagConstraints.WEST;
		gbc_lblCancelledLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblCancelledLabel.gridx = 2;
		gbc_lblCancelledLabel.gridy = 6;
		mainPanel.add(lblCancelledLabel, gbc_lblCancelledLabel);
		
		cancelledLabel = new JLabel("");
		cancelledLabel.setFont(new Font("Courier New", Font.BOLD, 11));
		GridBagConstraints gbc_cancelledLabel = new GridBagConstraints();
		gbc_cancelledLabel.anchor = GridBagConstraints.EAST;
		gbc_cancelledLabel.insets = new Insets(0, 0, 5, 5);
		gbc_cancelledLabel.gridx = 3;
		gbc_cancelledLabel.gridy = 6;
		mainPanel.add(cancelledLabel, gbc_cancelledLabel);
		
		JLabel lblUndeterminedLabel = new JLabel("�� ����������: ");
		lblUndeterminedLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
		GridBagConstraints gbc_lblUndeterminedLabel = new GridBagConstraints();
		gbc_lblUndeterminedLabel.anchor = GridBagConstraints.WEST;
		gbc_lblUndeterminedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblUndeterminedLabel.gridx = 2;
		gbc_lblUndeterminedLabel.gridy = 7;
		mainPanel.add(lblUndeterminedLabel, gbc_lblUndeterminedLabel);
		
		undeterminedLabel = new JLabel("");
		GridBagConstraints gbc_undeterminedLabel = new GridBagConstraints();
		gbc_undeterminedLabel.anchor = GridBagConstraints.EAST;
		gbc_undeterminedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_undeterminedLabel.gridx = 3;
		gbc_undeterminedLabel.gridy = 7;
		mainPanel.add(undeterminedLabel, gbc_undeterminedLabel);
		
		console = new JConsole();
		console.setFont(new Font("Courier New", Font.PLAIN, 11));
		JScrollPane scrollPane_console = new JScrollPane(console);
		scrollPane_console.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_console.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_console = new GridBagConstraints();
		gbc_console.anchor = GridBagConstraints.NORTH;
		gbc_console.fill = GridBagConstraints.BOTH;
		gbc_console.gridx = 0;
		gbc_console.gridy = 1;
		getContentPane().add(scrollPane_console, gbc_console);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("����");
		menuBar.add(fileMenu);
		
	    authMenuItem = new JMenuItem("�����������");
		authMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e){
				if(authMenuItem.isEnabled())
					autherization();
			}
		});
		authMenuItem.setEnabled(true);
		fileMenu.add(authMenuItem);
		
		infoCertMenuItem = new JMenuItem("���������� � �����������");
		infoCertMenuItem.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent evt){
				if(infoCertMenuItem.isEnabled())
					showInfoCertificate();
			}
		});
		infoCertMenuItem.setEnabled(false);
		fileMenu.add(infoCertMenuItem);
		
		fileMenu.addSeparator();
		
		connectMenuItem = new JMenuItem("����������");
		connectMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!authMenuItem.isEnabled()&&connectMenuItem.isEnabled())
					connect();
			}
		});
		connectMenuItem.setEnabled(false);
		fileMenu.add(connectMenuItem);
		
		disconnectMenuItem = new JMenuItem("���������");
		disconnectMenuItem.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(!authMenuItem.isEnabled()&&!connectMenuItem.isEnabled()&&disconnectMenuItem.isEnabled()){
					disconnect();
				}
			}
		});
		disconnectMenuItem.setEnabled(false);
		fileMenu.add(disconnectMenuItem);
		
		fileMenu.addSeparator();
		
		JMenuItem Settings = new JMenuItem("���������");
		Settings.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				new SettingsFrame().open();
			}
		});
		fileMenu.add(Settings);
		
		fileMenu.addSeparator();
		
		JMenuItem exitMenuItem = new JMenuItem("�����");
		exitMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				exit();
			}
		});
		fileMenu.add(exitMenuItem);
		
		JMenu listMenu = new JMenu("������ ����");
		menuBar.add(listMenu);
		
		loadFileMenuItem = new JMenuItem("��������� �� �����...");
		loadFileMenuItem.addMouseListener(new MouseAdapter() {
			@Override	
			public void mousePressed(MouseEvent evt) {
				if(loadFileMenuItem.isEnabled()){
					loadFile();
					selectYear();
				}
			}
		});
		loadFileMenuItem.setEnabled(false);
		listMenu.add(loadFileMenuItem);
		
		listMenu.addSeparator();
		
		updateStatusMenuItem = new JMenuItem("������ ���������� ��������");
		updateStatusMenuItem.addMouseListener(new MouseAdapter(){
			@Override 
			public void mousePressed(MouseEvent evt){
				if(updateStatusMenuItem.isEnabled()){
					UpdateEnStatus.updateFull();
					selectYear();
				}
			}
		});
		updateStatusMenuItem.setEnabled(false);
		listMenu.add(updateStatusMenuItem);
		
		fastUpdateStatusMenuItem = new JMenuItem("������� ���������� ��������");
		fastUpdateStatusMenuItem.addMouseListener(new MouseAdapter(){
			@Override 
			public void mousePressed(MouseEvent evt){
				if(fastUpdateStatusMenuItem.isEnabled()){
					UpdateEnStatus.updateFast();
					selectYear();
				}
			}
		});
		fastUpdateStatusMenuItem.setEnabled(false);
		listMenu.add(fastUpdateStatusMenuItem);
		
		listMenu.addSeparator();
		
		JMenu saveMenu = new JMenu("����� �� ����...");
		listMenu.add(saveMenu);
		
		saveOneDayMenuItem = new JMenuItem("... �� ���� ����");
		saveOneDayMenuItem.addMouseListener(new MouseAdapter(){
			@Override 
			public void mousePressed(MouseEvent evt){
				if(saveOneDayMenuItem.isEnabled()){
					new ReportOneDayFrame().open();
				}
			}
		});
		saveMenu.add(saveOneDayMenuItem);
		
		saveBetweenMenuItem = new JMenuItem("... �� ������");
		saveBetweenMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				if(saveBetweenMenuItem.isEnabled()){
					new ReportBetweenFrame().open();
				}
			}
		});
		saveMenu.add(saveBetweenMenuItem);
	}

	/**
	 * Processing methods fill combobox and labels
	 */	
	private void updateMainPanel(String year){
		if(yearComboBox.getModel().getSize() > 0){
			allInvoicesLabel.setText(String.valueOf(WorkingIncomingTable.Count.getCountAllInYear(year)));
			completedLabel.setText(String.valueOf(WorkingIncomingTable.Count.getCountCompletedInYear(year)));
			uncompletedLabel.setText(String.valueOf(WorkingIncomingTable.Count.getCountUncompletedInYear(year)));
			cancelledLabel.setText(String.valueOf(WorkingIncomingTable.Count.getCountCancelledInYear(year)));
			undeterminedLabel.setText(String.valueOf(WorkingIncomingTable.Count.getCountUndeterminedInYear(year)));
			
			List<MonthYearItem> list = WorkingIncomingTable.Date.selectMonthYear(year);
			if(list != null){
				model.clear();
				for(int index=0;index<list.size();index++){
					try {
						model.addElement(
								InvoiceDateFormat.string2DateReverseSmallDash(WorkingIncomingTable.Date.getStartMonthOfDate(list.get(index).getMonth(), list.get(index).getYear())), 
								InvoiceDateFormat.string2DateReverseSmallDash(WorkingIncomingTable.Date.getEndMonthOfDate(list.get(index).getMonth(), list.get(index).getYear())),
								WorkingIncomingTable.Count.getCountCompletedInMonthYear(list.get(index).getMonth(), list.get(index).getYear()), 
								WorkingIncomingTable.Count.getCountUncompletedInMonthYear(list.get(index).getMonth(), list.get(index).getYear()), 
								WorkingIncomingTable.Count.getCountCancelledInMonthYear(list.get(index).getMonth(), list.get(index).getYear()), 
								WorkingIncomingTable.Count.getCountUndeterminedInMonthYear(list.get(index).getMonth(), list.get(index).getYear())
						);

					} catch (ParseException e) {
						System.err.println("Record "+list.get(index).getMonth()+"-"+list.get(index).getYear()+": "+e.getLocalizedMessage());
					}
				}
			}else{
				JOptionPane.showMessageDialog(null, "���������� ���������� ������ ������","��������",JOptionPane.WARNING_MESSAGE);
			}
		}else{
			allInvoicesLabel.setText("0");
			completedLabel.setText("0");
			uncompletedLabel.setText("0");
			cancelledLabel.setText("0");
			undeterminedLabel.setText("0");
		}
	}
	
	private boolean fillYear(){
		List<String> list = WorkingIncomingTable.Date.selectYearInvoice();
		ComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		if(list == null){
			return false;
		}
		for(int index=0;index<list.size();index++){
			((DefaultComboBoxModel<String>) model).addElement(list.get(index));
		}
		yearComboBox.setModel(model);
		return true;
	}
	
	private void selectYear(){
		if(WorkingIncomingTable.Count.getCountAll() > 0){
			yearComboBox.setSelectedIndex(0);
			updateMainPanel(yearComboBox.getItemAt(yearComboBox.getSelectedIndex()));
		}
	}
	
	/**
	 * Methods autherization and connection to service
	 */
	private void autherization(){
		EVatServiceSingleton.getInstance().autherization(ApplicationProperties.getInstance());
		if(EVatServiceSingleton.getInstance().isAuthorized()){
			System.out.println("����������� ��������");
			authMenuItem.setEnabled(false);
			connectMenuItem.setEnabled(true);
			disconnectMenuItem.setEnabled(false);
			infoCertMenuItem.setEnabled(true);
			loadFileMenuItem.setEnabled(true);
			setTitle(title+" ["+ Certificate.getInstance().getOrgName().trim() +" " +Certificate.getInstance().getLastName().trim()+" "+Certificate.getInstance().getFirstMiddleName()+"]");
		}
	}
	
	private void connect(){
		if(EVatServiceSingleton.getInstance().isAuthorized()){
			EVatServiceSingleton.getInstance().connect();
			if(EVatServiceSingleton.getInstance().isConnected()){
				console.setText("");
				System.out.println("����������� ��������");
				System.out.println("����������� � ������� "+ApplicationProperties.getInstance().getUrlService()+" ��������� �������");
				connectMenuItem.setEnabled(false);
				disconnectMenuItem.setEnabled(true);
				
				updateStatusMenuItem.setEnabled(true);
				fastUpdateStatusMenuItem.setEnabled(true);
			}else{
				System.err.println("������ ����������� � ������� "+ApplicationProperties.getInstance().getUrlService());
			}
		}
	}
	
	private void disconnect(){
		if(EVatServiceSingleton.getInstance().isAuthorized()){
			if(EVatServiceSingleton.getInstance().isConnected()){
				EVatServiceSingleton.getInstance().disconnect();
				if(!EVatServiceSingleton.getInstance().isConnected()){
					System.out.println("���������� �� ������� "+ApplicationProperties.getInstance().getUrlService()+" ��������� �������");
					connectMenuItem.setEnabled(true);
					disconnectMenuItem.setEnabled(false);
					
					updateStatusMenuItem.setEnabled(false);
					fastUpdateStatusMenuItem.setEnabled(false);
				}else{
					System.err.println("������ ���������� �� ������� "+ApplicationProperties.getInstance().getUrlService());
				}
			}
		}
	}
	
	/*private void loadFile(){
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV files (.csv)", "csv"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int res = chooser.showDialog(null, "�������");
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				List<String> lines = null;
				if(res == JFileChooser.APPROVE_OPTION){
					try {
						lines = WorkingFiles.readCSVFile(chooser.getSelectedFile());
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),"������",JOptionPane.ERROR_MESSAGE);
					}
					if(lines != null){
						int avialCount = 0;
						int errorCount = 0;
						int notavialCount = 0;
						int updateCount = 0;;
						LoadFileProgressBar progress = new LoadFileProgressBar(lines.size()).activated();
						//�������� ������� ��� � �����������
						//String unp = "400047886";
						String unp = "";
						if(Certificate.getInstance().getUnp2() == ""){//���� unp2 ������
							if(Certificate.getInstance().getUnp101() == ""){//���� unp101 ������
								progress.disactivated();
								JOptionPane.showMessageDialog(null, "�� ��������� ���. �������� ��������","������",JOptionPane.ERROR_MESSAGE);
							}else{
								unp = Certificate.getInstance().getUnp101();
							}
						}else{
							unp = Certificate.getInstance().getUnp2();
						}
						try{
							int limit = 30;//���������� ��������
							String dateIssue = "";
							String dateCommission = "";
							String dateSignature = "";
							String dateCancellation = "";
							String dateDocument = "";
							
							for(int index=0; index<lines.size();index++){
								String[] fields = lines.get(index).split(";",limit);
								if(fields[0].trim().equals(unp)){//�������� �� ������ �����������
									System.out.println("������ "+(index++)+": ������� ������ ������ ��������� ���� �� �����");
								}else{
									switch(WorkingIncomingTable.Count.getCountRecord(fields[12])){
									case -1: JOptionPane.showMessageDialog(null, "������ �������� ������� ������ ���� "+fields[12]+" � �������","������",JOptionPane.ERROR_MESSAGE); errorCount++; break;
									case  0: if(WorkingIncomingTable.Insert.insertIncoming(fields)) {notavialCount++;}else{errorCount++;} break;
									case  1: {
										
										if(fields[15].trim().length() > 0){
											dateIssue = InvoiceDateFormat.dateReverseSmallDash2String(InvoiceDateFormat.string2DateSmallDash(fields[15]));
										}else{
											dateIssue = fields[15];
										}
										
										if(fields[16].trim().length() > 0){
											dateCommission = InvoiceDateFormat.dateReverseSmallDash2String(InvoiceDateFormat.string2DateSmallDash(fields[16]));
										}else{
											dateCommission = fields[16];
										}
										
										if(fields[17].trim().length() > 0){
											dateSignature = InvoiceDateFormat.dateReverseSmallDash2String(InvoiceDateFormat.string2DateSmallDash(fields[17]));
										}else{
											dateSignature = fields[17];
										}
										
										if(fields[19].trim().length() > 0){
											dateCancellation = InvoiceDateFormat.dateReverseSmallDash2String(InvoiceDateFormat.string2DateSmallDash(fields[19]));
										}else{
											dateCancellation = fields[19];
										}
									
										dateDocument = fields[29];
										
										if(WorkingIncomingTable.Update.updateStatusFromFile(fields[14], fields[12])
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATEISSUE", dateIssue, fields[12])
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATECOMMISSION", dateCommission, fields[12]))
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATESIGNATURE", dateSignature, fields[12]))
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATECANCELLATION", dateCancellation, fields[12]))
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATEDOCUMENT", dateDocument, fields[12])))){updateCount++;}else{errorCount++;} break;}
									default: avialCount++; break;
									}
									progress.setProgress(index+1);		
									if(progress.isCancelled()){
										JOptionPane.showMessageDialog(null, "�������� ����� ��������","��������",JOptionPane.WARNING_MESSAGE);
										break;
									}
								}
							}
						} catch (SQLException | ParseException e) {
							JOptionPane.showMessageDialog(null, e.getLocalizedMessage()+System.lineSeparator()+"�������� ����� ��������","������",JOptionPane.ERROR_MESSAGE);
							progress.disactivated();
						}
						JOptionPane.showMessageDialog(null, "��������� "+notavialCount+" ����"+System.lineSeparator()+
								"�� ��������� ��-�� �� ������������ "+avialCount+" ����"+System.lineSeparator()+
								"��������� ������� �� ����� � " + updateCount + " ����"+System.lineSeparator()+
								"�� ��������� ��-�� ������ "+errorCount+" ����","����������",JOptionPane.INFORMATION_MESSAGE);
						progress.disactivated();
					}else{
						JOptionPane.showMessageDialog(null, "�������� ���� �������� ���������"+System.lineSeparator()+
								"�������� ������ ����","������",JOptionPane.ERROR_MESSAGE);
					}
				}
				return null;		
			}			
		};	
		worker.execute();
	}*/
	
	private void loadFile(){
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV files (.csv)", "csv"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int res = chooser.showDialog(null, "�������");
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				List<String> lines = null;
				if(res == JFileChooser.APPROVE_OPTION){
					try {
						lines = WorkingFiles.readCSVFile(chooser.getSelectedFile());
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),"������",JOptionPane.ERROR_MESSAGE);
					}
					if(lines != null){
						int avialCount = 0;
						int errorCount = 0;
						int notavialCount = 0;
						int updateCount = 0;;
						LoadFileProgressBar progress = new LoadFileProgressBar(lines.size()).activated();
						//�������� ������� ��� � �����������
						//String unp = "400047886";
						String unp = "";
						if(Certificate.getInstance().getUnp2() == ""){//���� unp2 ������
							if(Certificate.getInstance().getUnp101() == ""){//���� unp101 ������
								progress.disactivated();
								JOptionPane.showMessageDialog(null, "�� ��������� ���. �������� ��������","������",JOptionPane.ERROR_MESSAGE);
							}else{
								unp = Certificate.getInstance().getUnp101();
							}
						}else{
							unp = Certificate.getInstance().getUnp2();
						}
						try{
							int limit = ApplicationConstants.CSV_COUNTCOLUMNS;//���������� ��������
							String dateIssue = "";
							String dateCommission = "";
							String dateSignature = "";
							String dateCancellation = "";
							String dateDocument = "";
							
							for(int index=0; index<lines.size();index++){
								String[] fields = lines.get(index).split(";",limit);
								if(fields[1].trim().equals(unp)){//�������� �� ������ �����������
									System.out.println("������ "+(index++)+": ������� ������ ������ ��������� ���� �� �����");
								}else{
									switch(WorkingIncomingTable.Count.getCountRecord(fields[13])){
									case -1: JOptionPane.showMessageDialog(null, "������ �������� ������� ������ ���� "+fields[13]+" � �������","������",JOptionPane.ERROR_MESSAGE); errorCount++; break;
									case  0: if(WorkingIncomingTable.Insert.insertIncoming(fields)) {notavialCount++;}else{errorCount++;} break;
									case  1: {
										
										if(fields[17].trim().length() > 0){
											dateIssue = InvoiceDateFormat.dateReverseSmallDash2String(InvoiceDateFormat.string2DateSmallDash(fields[17]));
										}else{
											dateIssue = fields[17];
										}
										
										if(fields[18].trim().length() > 0){
											dateCommission = InvoiceDateFormat.dateReverseSmallDash2String(InvoiceDateFormat.string2DateSmallDash(fields[18]));
										}else{
											dateCommission = fields[18];
										}
										
										if(fields[19].trim().length() > 0){
											dateSignature = InvoiceDateFormat.dateReverseSmallDash2String(InvoiceDateFormat.string2DateSmallDash(fields[19]));
										}else{
											dateSignature = fields[19];
										}
										
										if(fields[20].trim().length() > 0){
											dateCancellation = InvoiceDateFormat.dateReverseSmallDash2String(InvoiceDateFormat.string2DateSmallDash(fields[20]));
										}else{
											dateCancellation = fields[20];
										}
									
										dateDocument = fields[36];
										
										if(WorkingIncomingTable.Update.updateStatusFromFile(fields[16], fields[13])
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATEISSUE", dateIssue, fields[13])
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATECOMMISSION", dateCommission, fields[13]))
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATESIGNATURE", dateSignature, fields[13]))
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATECANCELLATION", dateCancellation, fields[13]))
											&&(WorkingIncomingTable.Update.updateDateFromFile("DATEDOCUMENT", dateDocument, fields[13])))){updateCount++;}else{errorCount++;} break;}
									default: avialCount++; break;
									}
									progress.setProgress(index+1);		
									if(progress.isCancelled()){
										JOptionPane.showMessageDialog(null, "�������� ����� ��������","��������",JOptionPane.WARNING_MESSAGE);
										break;
									}
								}
							}
						} catch (SQLException | ParseException e) {
							JOptionPane.showMessageDialog(null, e.getLocalizedMessage()+System.lineSeparator()+"�������� ����� ��������","������",JOptionPane.ERROR_MESSAGE);
							progress.disactivated();
						}
						JOptionPane.showMessageDialog(null, "��������� "+notavialCount+" ����"+System.lineSeparator()+
								"�� ��������� ��-�� �� ������������ "+avialCount+" ����"+System.lineSeparator()+
								"��������� ������� �� ����� � " + updateCount + " ����"+System.lineSeparator()+
								"�� ��������� ��-�� ������ "+errorCount+" ����","����������",JOptionPane.INFORMATION_MESSAGE);
						progress.disactivated();
					}else{
						JOptionPane.showMessageDialog(null, "�������� ���� �������� ���������"+System.lineSeparator()+
								"�������� ������ ����","������",JOptionPane.ERROR_MESSAGE);
					}
				}
				return null;		
			}			
		};	
		worker.execute();
	}
		
	private void exit(){
		String textDialog;
		if(EVatServiceSingleton.getInstance().isAuthorized()){
			textDialog = "��������� ������ ���������?"+System.lineSeparator()+"�������������� ����� ����� ������";
		}else{
			textDialog = "��������� ������ ���������?";
		}
		
		if(JOptionPane.showConfirmDialog(null, textDialog,"���������� ������",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
			System.exit(1);
		}
	}
	
	private void showInfoCertificate(){
		new ShowCertificateFrame().open();
	}
	
	
}
