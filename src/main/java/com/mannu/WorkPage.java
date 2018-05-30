package com.mannu;

import java.awt.Toolkit;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JFrame;

import DbConnection.DbConn;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdesktop.swingx.JXDatePicker;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WorkPage extends Thread{
	private Connection con;
	private String usnam;
	private JXDatePicker fdate,tdate;
	private JLabel lblStatus;
	private Document document;
	private PdfWriter writer;
	private JTable table;
	java.util.List<FileDetail> fileDetails;
	DefaultTableModel model;

	public WorkPage(String string) {
		this.usnam=string;
		this.con=DbConn.nser();
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void run() {
		File temp2=new File("C:\\temp2\\");
		if (!temp2.exists()) {
			temp2.mkdir();
		}
		
		Toolkit tk=Toolkit.getDefaultToolkit();
		double w=tk.getScreenSize().getWidth();
		double h=tk.getScreenSize().getHeight();
		JFrame frame=new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					String baseUrl = FilenameUtils.getPath("\\\\srv-kdms-file2\\images");
					File fname=new File(baseUrl);
					Runtime rt = Runtime.getRuntime();
					if(!fname.exists()) {
						rt.exec(new String[] {"cmd.exe","/c","net use \\\\srv-kdms-file2\\images /user:logicalaccess@karvy.com India@123"});
					}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
			}
		});
		frame.setLocation((int)w/3, (int)h/5);
		frame.setTitle("Welcome "+usnam);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(374, 361);
		frame.getContentPane().setLayout(null);
		
		fdate = new JXDatePicker();
		fdate.setFormats(new String[] {"yyyy-MM-dd"});
		fdate.setDate(new Date());
		fdate.setBounds(80, 11, 105, 22);
		frame.getContentPane().add(fdate);
		
		JLabel lblFromDate = new JLabel("From Date:");
		lblFromDate.setBounds(10, 15, 69, 14);
		frame.getContentPane().add(lblFromDate);
		
		JLabel lblToDate = new JLabel("To Date:");
		lblToDate.setBounds(190, 15, 58, 14);
		frame.getContentPane().add(lblToDate);
		
		tdate = new JXDatePicker();
		tdate.setFormats(new String[] {"yyyy-MM-dd"});
		tdate.setDate(new Date());
		tdate.setBounds(251, 11, 105, 22);
		frame.getContentPane().add(tdate);
		
		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				for(int i=0; i<model.getRowCount();i++) {
					try {
						GeneratePdf(model.getValueAt(i, 1));
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (DocumentException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				JOptionPane.showMessageDialog(null, "Completed", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnExport.setBounds(241, 44, 89, 23);
		frame.getContentPane().add(btnExport);
		
		lblStatus = new JLabel("Status:");
		lblStatus.setBounds(29, 307, 312, 14);
		frame.getContentPane().add(lblStatus);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model=(DefaultTableModel) table.getModel();
				if(model.getRowCount()>0) {
					model.setRowCount(0);
				}
				System.out.println("Date: "+fdate.getDate()+"^"+tdate.getDate());
				DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				System.out.println("LL: "+df.format(fdate.getDate()).toString()+"^"+df.format(tdate.getDate()).toString());
				try {
					if(con.isClosed()) {
						con=DbConn.nser();
					}
					
					ResultSet rs=null;
					CallableStatement cb=con.prepareCall("{call panchmov (?,?)}",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
					cb.setString(1,  df.format(fdate.getDate()).toString());
					cb.setString(2, df.format(tdate.getDate()).toString());
					boolean result=cb.execute();
					int roweff=0;
					while (result || roweff !=-1) {
						if(result) {
							rs=cb.getResultSet();
							break;
						} else {
							roweff=cb.getUpdateCount();
						}
						result=cb.getMoreResults();
					}
					model=(DefaultTableModel) table.getModel();
					model.setRowCount(0);
					Object[] row=new Object[2];
					int a=0;
					while (rs.next()) {
						a=1+a;
						row[0]=a;
						row[1]=rs.getString(1);
						model.addRow(row);
					}
					rs.close();
					cb.close();
					con.close();
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		btnSubmit.setBounds(142, 44, 89, 23);
		frame.getContentPane().add(btnSubmit);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 71, 346, 225);
		frame.getContentPane().add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Sl No", "Acknowledgement Number"
			}
		));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		scrollPane.setViewportView(table);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				model=(DefaultTableModel) table.getModel();
				model.setRowCount(0);
				Object[] exrow=new Object[2];
				int returnValue = jfc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					System.out.println(selectedFile.getAbsolutePath());
					if(selectedFile.getAbsolutePath().endsWith(".xlsx")) {
						try {
							if(con.isClosed()) {
								con=DbConn.nser();
							}
							XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(selectedFile.getAbsolutePath()));
							XSSFSheet sheet = book.getSheetAt(0); 
							Iterator<Row> itr = sheet.iterator();
							int p=0;
							while (itr.hasNext()) {
								Row row = itr.next();
								Iterator<Cell> cellIterator = row.cellIterator();
								while (cellIterator.hasNext()) {
									p=p+1;
									exrow[0]=p;
									Cell cell = cellIterator.next();
									switch (cell.getCellType()) {
									case Cell.CELL_TYPE_STRING:
										exrow[1]=cell.getStringCellValue();
										model.addRow(exrow);
				                        break;
									case Cell.CELL_TYPE_NUMERIC: 
										System.out.print(cell.getNumericCellValue() + "\t");
									break; 
									case Cell.CELL_TYPE_BOOLEAN:
										System.out.print(cell.getBooleanCellValue() + "\t"); 
										break; 
									default:
										
									}
								}
								System.out.println("");
							}
							
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "Unknown file format");
					}
					
				}
				
			}
		});
		btnBrowse.setBounds(39, 44, 89, 23);
		frame.getContentPane().add(btnBrowse);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	protected void GeneratePdf(Object valueAt) throws DocumentException, IOException, SQLException {
		if(con.isClosed()) {
			con=DbConn.nser();
		}
		System.out.println("ACkno: "+valueAt.toString().trim());
		ResultSet rs=null;
		CallableStatement cb=con.prepareCall("{call getpath (?)}",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		cb.setString(1,  valueAt.toString().trim());
		boolean result=cb.execute();
		int roweff=0;
		while (result || roweff !=-1) {
			if(result) {
				rs=cb.getResultSet();
				break;
			} else {
				roweff=cb.getUpdateCount();
			}
			result=cb.getMoreResults();
		}
		java.util.List<String> ffl=new ArrayList<String>();
		ffl.clear();
		while (rs.next()) {
			ffl.add(rs.getString(1));
		}
		rs.close();
		cb.close();
		con.close();
		if (ffl.size()>0) {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\temp2\\"+valueAt.toString()+".pdf"));
			document.open();
			for (String fil: ffl) {
				Image img=Image.getInstance(fil);
				document.setPageSize(new Rectangle(img.getWidth(), img.getHeight()));
				document.setMargins(0, 0, 0, 0);
				document.newPage();
				document.add(img);
			}
			document.close();
			writer.close();	
		}
	}
}
