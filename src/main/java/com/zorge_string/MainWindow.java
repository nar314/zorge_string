package com.zorge_string;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;

public class MainWindow {

	private JFrame frame = new JFrame("Zorge string 1.0");
	private JPasswordField tfPsw = new JPasswordField(20);	
	private JTextArea taInput = new JTextArea();
	private JTextArea taOutput = new JTextArea();
	private JComboBox<String> cbWhat = new JComboBox<String>(new String[] {"Encrypt", "Decrypt"});
	private boolean isEncrypt = true;
	private JButton btnDo = new JButton("Encrypt");
	
	private Color colorGreen = new Color(204, 255, 204);
	private Color colorRed = new Color(255, 204, 204);

	//CoderCBC coder = new CoderCBC(); // in case you want to use CBC	
	private CoderGCM coder = new CoderGCM();
	
	/**
	 * Constructor.
	 */
	public MainWindow() {
		
		createGUI();
	}
	
	/**
	 * Show main window.
	 */
	public void show() {
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame.pack();
		frame.setSize(480, 380);
		frame.setMinimumSize(new Dimension(480, 380));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);		
	}
	
	private JPanel panelInput() {
		
		JPanel panelPassword = panelPassword();
		
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		
		JScrollPane sp = new JScrollPane(taInput);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JLabel label = new JLabel("Input");
		panel.add(panelPassword);
		panel.add(label);
		panel.add(sp);

		layout.putConstraint(SpringLayout.NORTH, panelPassword, 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, panelPassword, 5, SpringLayout.WEST, panel);
		
		layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.SOUTH, panelPassword);
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, panel);
		
		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, sp, 0, SpringLayout.SOUTH, panel);
		
		return panel;
	}
	
	private JPanel panelOutput() {
		
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		
		JScrollPane sp = new JScrollPane(taOutput);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JLabel label = new JLabel("Output");
		panel.add(label);
		panel.add(sp);

		layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, panel);
		
		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, sp, 0, SpringLayout.SOUTH, panel);
		
		return panel;
	}
	
	private JPanel panelPassword() {
		
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 5);
		panel.add(new JLabel("Password:"), gbc);
		
		gbc.gridx = 1;
		gbc.insets = new Insets(0, 0, 0, 5);
		panel.add(tfPsw, gbc);
		
		gbc.gridy = 0;
		gbc.gridx = 2;
		gbc.insets = new Insets(5, 0, 0, 5);
		panel.add(cbWhat, gbc);

		gbc.gridy = 0;		
		gbc.gridx = 3;
		panel.add(btnDo, gbc);
		
		return panel;
	}

	private void createGUI() {
		
		JPanel panelInput = panelInput();
		JPanel panelOutput = panelOutput();
		
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelInput, panelOutput);
		sp.setDividerLocation(180);
		
		frame.getContentPane().add(sp);
		addMenu();
		
		taOutput.setEditable(false);
		
		btnDo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doIt();
			}
		});
		
		cbWhat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String mode = (String)cbWhat.getSelectedItem();
				btnDo.setText(mode);
				isEncrypt = mode.equals("Encrypt");
			}
		});
	}
	
	private void doIt() {
		
		taOutput.setText("");
		
		final String psw = new String(tfPsw.getPassword());
		if(psw.isEmpty()) {
			tfPsw.requestFocus();
			Utils.MessageBox_Error("Password is empty.");
			return;
		}
		
		final String input = taInput.getText();
		if(input.isEmpty()) {
			taInput.requestFocus();
			Utils.MessageBox_Error("Input is empty.");
			return;
		}
		
		try {
			final String out = coder.doIt(input, psw, isEncrypt);
			taOutput.setText(out);
			taOutput.setBackground(colorGreen);
		}
		catch(Exception e) {
			taOutput.setBackground(colorRed);
			Utils.MessageBox_Error("Error.");
		}
	}
	
	private void addMenu() {

		JMenuBar menuBar = new JMenuBar();
		JMenu menuHelp = new JMenu("Help");
		JMenuItem miAbout = new JMenuItem("About");
		
		menuHelp.add(miAbout);
		menuBar.add(menuHelp);
		frame.setJMenuBar(menuBar);
		
		miAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DlgAbout dlg = new DlgAbout();
				dlg.showDialog();
			}
		});
	}
}
