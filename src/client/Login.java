package client;
/* Login Class */
/* This class creates a gui that allows players 
 * to enter ip and username. It will eventually
 * not have the ip and be replaced by passwords */

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	
	JPanel top, bottom, center;
	JLabel address, username, pass;
	JTextField addressfield, userfield, passfield, portfield; 
	JButton loginButton;
	
	static boolean successful = false;
	
	public Login()
	{
		//setup JFrame
		super("Login");
		setPreferredSize(new Dimension(350, 250));
		setSize(new Dimension(340, 120));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new GridLayout(3, 1));
		
		//set up GUI
		top = new JPanel(new FlowLayout());
		address = new JLabel("IP Address:");
		top.add(address);
		addressfield = new JTextField(12);
		addressfield.setText("localhost");
		top.add(addressfield);
		portfield = new JTextField(6);
		portfield.setText("8080");
		portfield.setEditable(false);
		top.add(portfield);
		
		add(top);
		
		center = new JPanel(new FlowLayout());
		username = new JLabel("Username:");
		center.add(username);
		userfield = new JTextField(18);
		userfield.setText("User");
		center.add(userfield);
		
		add(center);
		
		bottom = new JPanel(new FlowLayout());
		pass = new JLabel("Password:");
		bottom.add(pass);
		passfield = new JTextField("Disabled               ");
		passfield.setEditable(false);
		bottom.add(passfield);
		
		//set up action event for login button
		loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Login.successful = true;
					new Client(addressfield.getText(), Integer.valueOf(portfield.getText()), userfield.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		bottom.add(loginButton);
		
		this.add(bottom);
		
		this.setVisible(true);
	}
	
    //checks to see if button should be active
	public void update()
	{
		if (userfield.getText().length() > 15)
		{
			userfield.setText(userfield.getText().substring(0, 15));
			userfield.setCaretPosition(15);
		}
		if (addressfield.getText().equals("")  || userfield.getText().equals(""))
		{	
			loginButton.setEnabled(false);
		}
		else 
		{
			loginButton.setEnabled(true);		
		}
	}

	//returns if connection was successful
	public boolean getSuccessful()
	{
		return successful;
	}
	
	
}
