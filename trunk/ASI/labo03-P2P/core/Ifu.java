package core;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Un simple client mail avec une GUI pour envoyer des emails.
 */
public class Ifu extends Frame {
	/* Elements du GUI. */
	private Button btSend = new Button("Envoyer");
	private Button btClear = new Button("Effacer");
	private Button btQuit = new Button("Quitter");
	private Label SMTPServerLabel = new Label("Serveur SMTP:");

	private TextField SMTPServerField = new TextField("", 40); 
	private Label fromLabel = new Label("De:");
	private TextField fromField = new TextField("", 40);
	private Label toLabel = new Label("A:"); 
	private TextField toField = new TextField("", 40);
	// champs CC
	private Label ccLabel = new Label("CC:"); 
	private TextField ccField = new TextField("", 40);
	// champs BCC
	private Label bccLabel = new Label("BCC:"); 
	private TextField bccField = new TextField("", 40);
	
	private Label subjectLabel = new Label("Sujet:");
	private TextField subjectField = new TextField("", 40);
	private Label messageLabel = new Label("Message:");
	private TextArea messageText = new TextArea(10, 40);

	/**
	 * Construire la frame avec les differents champs
	 * (Serveur SMTP, De, A, Sujet et message).
	 */
	public MailClient() {
		super("Java Mailclient");

		/* Creer les panneaux pour contenir les champs.
	   Un panneau supplémentaire est créé pour contenir les
	   panneaux enfant. */
		Panel SMTPServerPanel = new Panel(new BorderLayout());
		Panel fromPanel = new Panel(new BorderLayout());
		Panel toPanel = new Panel(new BorderLayout());
		// on defini une zone pour le CC et le BCC
		Panel ccPanel = new Panel(new BorderLayout());
		Panel bccPanel = new Panel(new BorderLayout());
		Panel subjectPanel = new Panel(new BorderLayout());
		Panel messagePanel = new Panel(new BorderLayout());
		SMTPServerPanel.add(SMTPServerLabel, BorderLayout.WEST);
		SMTPServerPanel.add(SMTPServerField, BorderLayout.CENTER);
		fromPanel.add(fromLabel, BorderLayout.WEST);
		fromPanel.add(fromField, BorderLayout.CENTER);
		toPanel.add(toLabel, BorderLayout.WEST);
		toPanel.add(toField, BorderLayout.CENTER);
		// on ajoute le champs CC et BCC à la zone prévue a cet effet
		ccPanel.add(ccLabel, BorderLayout.WEST);
		ccPanel.add(ccField, BorderLayout.CENTER);
		bccPanel.add(bccLabel, BorderLayout.WEST);
		bccPanel.add(bccField, BorderLayout.CENTER);

		subjectPanel.add(subjectLabel, BorderLayout.WEST);
		subjectPanel.add(subjectField, BorderLayout.CENTER);
		messagePanel.add(messageLabel, BorderLayout.NORTH);	
		messagePanel.add(messageText, BorderLayout.CENTER);
		Panel fieldPanel = new Panel(new GridLayout(0, 1));
		fieldPanel.add(SMTPServerPanel);
		fieldPanel.add(fromPanel);
		fieldPanel.add(toPanel);
		fieldPanel.add(ccPanel);
		fieldPanel.add(bccPanel);
		fieldPanel.add(subjectPanel);

		/* Créer un panneau pour les boutons et ajouter les
	   écouteurs aux boutons. */
		Panel buttonPanel = new Panel(new GridLayout(1, 0));
		btSend.addActionListener(new SendListener());
		btClear.addActionListener(new ClearListener());
		btQuit.addActionListener(new QuitListener());
		buttonPanel.add(btSend);
		buttonPanel.add(btClear);
		buttonPanel.add(btQuit);

		/* Ajouter, paqueter et montrer. */
		add(fieldPanel, BorderLayout.NORTH);
		add(messagePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
		show();
	}

	static public void main(String argv[]) {
		new MailClient();
	}

	/* Traitement pour le bouton Envoyer. */
	class SendListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Envoi de l'email");

			/* Premièrement, vérifier que nous avons le serveur
	       SMTP et les adresses emails source et destination.*/
			if((SMTPServerField.getText()).equals("")) {
				System.out.println("Pas de SMTP Server!");
				return;
			}
			if((fromField.getText()).equals("")) {
				System.out.println("Pas d'adresse source!");
				return;
			}
			if((toField.getText()).equals("")) {
				System.out.println("Pas d'adresse destination!");
				return;
			}
  
			/* Créer le message */
			Message mailMessage = new Message(
					SMTPServerField.getText(),
					fromField.getText(), 
					toField.getText(), 
					ccField.getText(),
					bccField.getText(),
					subjectField.getText(), 
					messageText.getText());

			/* Contrôler que le message est valide (cf classe Message).*/
			if(!mailMessage.isValid()) {
				return;
			}

			/* Créer l'enveloppe, ouvrir la connexion et tenter
	       d'envoyer le message. */
			Envelope envelope = new Envelope(mailMessage);
			System.out.println(envelope.toString());
			try {
				SMTPConnection connection = new SMTPConnection(envelope);
				connection.send(envelope);
				connection.close();
			} catch (IOException error) {
				System.out.println("L'envoi du message a echoue: "
						+ error);
				return;
			}
			System.out.println("L'envoi du message a reussi!");
		}
	}

	/* Effacer les champs du GUI. */
	class ClearListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("Effacement des champs");
			SMTPServerField.setText("");
			fromField.setText("");
			toField.setText("");
			ccField.setText("");
			bccField.setText("");
			subjectField.setText("");
			messageText.setText("");
		}
	}

	/* Quitter. */
	class QuitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
}