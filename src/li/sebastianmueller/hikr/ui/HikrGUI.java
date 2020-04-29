package li.sebastianmueller.hikr.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import li.sebastianmueller.hikr.extractors.ExtractHTML;
import li.sebastianmueller.hikr.util.Util;

public class HikrGUI extends JFrame implements HikrListener {

	private static final long serialVersionUID = 5918920477579454552L;

	private double payloadSum = 0;
	
	private JButton startButton;
	private JProgressBar progressBar;
	private JLabel message;
	private JLabel payloadStatus;
	private JTextField name;
	private JPanel containerPanel;
	
	public HikrGUI() {
		super("hikr.org Backup");
		
		containerPanel = new JPanel();
		containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		containerPanel.setLayout(new GridLayout(4, 0, 20, 20));
		this.getContentPane().add(containerPanel);
		
		ExtractHTML.addListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JPanel initPanel1(JPanel panel) {
		panel.setLayout(new GridLayout(1,2));

		JLabel nameTitle = new JLabel("hikr.org Benutzername:");
		panel.add(nameTitle);
		
		name = new JTextField();
		panel.add(name);
		name.setPreferredSize(new Dimension(200, 20));
		name.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				check();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				check();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				check();
			}

			private void check() {
				if (name.getText().equals("")) {
					startButton.setEnabled(false);
				} else {
					startButton.setEnabled(true);
				}
			}
			
		});
		
		return panel;
	}
	
	private JPanel initPanel2(JPanel panel) {
		panel.setLayout(new GridLayout(1,2));

		startButton = new JButton("Starten");
		startButton.setEnabled(false);
		panel.add(startButton);
		startButton.addActionListener(event -> {

				startButton.setEnabled(false);

				Thread t = new Thread() {
					public synchronized void run() {
						try {
							ExtractHTML.parse(name.getText(), 215);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				t.start();


		});
		
		JButton cancel = new JButton("Abbrechen");
		panel.add(cancel);
		cancel.addActionListener(e -> finish());
		
		return panel;
	}
	
	private JPanel initPanel3(JPanel panel) {
		panel.setLayout(new GridLayout(1,1));

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(false);
		panel.add(progressBar);
		
		return panel;
	}
	
	private JPanel initPanel4(JPanel panel) {
		panel.setLayout(new GridLayout(1,2));

		message = new JLabel();
		panel.add(message);	
		
		payloadStatus = new JLabel("0 MB");
		payloadStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(payloadStatus);
		
		return panel;
	}
	
	public void init() {
		containerPanel.add(initPanel1(new JPanel()));
		containerPanel.add(initPanel2(new JPanel()));
		containerPanel.add(initPanel3(new JPanel()));
		containerPanel.add(initPanel4(new JPanel()));
		
		this.pack();
		this.setVisible(true);
	}

	protected void finish() {
		this.dispose();
		System.exit(0);
	}

	@Override
	public void updateStatus(double count, double total, String messageString) {
		progressBar.setStringPainted(true);
		progressBar.setValue((int) Util.round(count / total * 100, 2));
		progressBar.setString("Tour " + (int) count + " von " + (int) total);	
		message.setText(messageString);
	}

	@Override
	public void done() {
		progressBar.setValue(0);
		startButton.setEnabled(true);
		message.setText("Backup erfolgreich abgeschlossen!");
	}

	@Override
	public void updateMessage(String messageString) {
		message.setText(messageString);
	}
	
	@Override
	public void addPayload(double payloadSize) {
		payloadSum += payloadSize;
		payloadStatus.setText(Util.round(payloadSum / 1024.0, 2) + " MB");
	}
}