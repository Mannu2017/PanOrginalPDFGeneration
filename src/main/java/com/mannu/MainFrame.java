package com.mannu;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.CSS;

import DbConnection.DbConn;

import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame{
	
	private String[] args;
	private JTextField textField;
	private JPasswordField passwordField;
	private Connection con;

	public void setArgs(String[] args) {
		this.args=args;
		this.con=DbConn.ondser();
	}

	public MainFrame() {
		Toolkit tk=Toolkit.getDefaultToolkit();
		double w=tk.getScreenSize().getWidth();
		double h=tk.getScreenSize().getHeight();
		setTitle("Pran Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Login Page", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 274, 124);
		getContentPane().add(panel);
		
		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setBounds(25, 29, 68, 14);
		panel.add(lblUserName);
		
		textField = new JTextField();
		textField.setBounds(106, 26, 134, 20);
		panel.add(textField);
		textField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(25, 54, 68, 14);
		panel.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(106, 51, 134, 20);
		panel.add(passwordField);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textField.getText().isEmpty()) 
				{
					JOptionPane.showMessageDialog(null, "Please enter username", "Info", JOptionPane.INFORMATION_MESSAGE);
					textField.requestFocus();
				} else if (passwordField.getText().isEmpty()) 
				{
					JOptionPane.showMessageDialog(null, "Please enter password", "Info", JOptionPane.INFORMATION_MESSAGE);
					passwordField.requestFocus();
				} else {
					try {
						if(con.isClosed()) {
							con=DbConn.ondser();
						}
						ResultSet rs=null;
						CallableStatement cb=con.prepareCall("{call pranlog (?,?)}",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
						cb.setString(1, textField.getText());
						cb.setString(2, passwordField.getText());
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
						while(rs.next()) {
							if(rs.getString(1).equals("2")) {
								JOptionPane.showMessageDialog(null, "invalid username or password", "Info", JOptionPane.INFORMATION_MESSAGE);
								textField.setText("");
								passwordField.setText("");
								textField.requestFocus();
							} else if (rs.getString(1).equals("1")) {
								WorkPage wp=new WorkPage(rs.getString(2));
								wp.start();
								dispose();
							}
							
						}
						cb.close();
						rs.close();
						con.close();
						
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
		btnLogin.setBounds(35, 79, 89, 23);
		panel.add(btnLogin);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnClose.setBounds(136, 79, 89, 23);
		panel.add(btnClose);
		setLocation((int)w/3, (int)h/3);
		setSize(300, 174);
		setResizable(false);
		setVisible(true);	
	}
}
