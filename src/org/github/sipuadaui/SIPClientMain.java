package org.github.sipuadaui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.github.sipuada.Sipuada;
import org.github.sipuada.SipuadaApi.BasicRequestCallback;
import org.github.sipuada.SipuadaApi.CallInvitationCallback;
import org.github.sipuada.SipuadaApi.SipuadaListener;
import org.github.sipuada.plugins.audio.LibJitsiMediaSipuadaPlugin;

import net.miginfocom.swing.MigLayout;

public class SIPClientMain implements SipuadaListener {

	private JFrame frmSipuada;
	private JTextField registrarDomainTextField;
	private JTextField registrarUserNameTextField;
	private JTextField callerDomainTextField;
	private JTextField callerUserTextField;
	private JTextField passwordField;
	private JTextArea textArea;
	private Sipuada sipuada;
	private String currentCallID;
	private String currentInviteCallID;
	private JButton btAcceptCall;
	private JButton btRejectCall;
	private boolean isBusy = false;
	private JButton btnCancel;
	private JButton btnEndCall;
	private JButton btCall;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SIPClientMain window = new SIPClientMain();
					window.frmSipuada.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SIPClientMain() {
		initialize();
		setDefautValues();
	}

	private void setDefautValues() {
		registrarDomainTextField.setText("10.100.100.125:5060");
		registrarUserNameTextField.setText("xibaca");
		passwordField.setText("xibaca");
		callerDomainTextField.setText("10.100.100.125:5060");
	}

	private void setUPCallButton(final JButton callButton) {

		callButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (sipuada == null) {
					textArea.setText(textArea.getText() + System.getProperty("line.separator") + " - "
							+ " Required to register!");
					btnCancel.setEnabled(false);
					callButton.setEnabled(true);
				}
				callButton.setEnabled(false);
				sipuada.inviteToCall(callerUserTextField.getText(), callerDomainTextField.getText(),
						new CallInvitationCallback() {
					@Override
					public void onWaitingForCallInvitationAnswer(String localUser, String localDomain, String callId) {
						textArea.setText(textArea.getText() + System.getProperty("line.separator") + " - "
								+ " Waiting For Call InvitationAnswer ...");
						currentCallID = callId;
						btnCancel.setEnabled(true);
					}

					@Override
					public void onCallInvitationRinging(String localUser, String localDomain, String callId,
							boolean shouldExpectEarlyMedia) {
						textArea.setText(textArea.getText() + System.getProperty("line.separator") + " - "
							+ (shouldExpectEarlyMedia ? " Expecting early media..." : " Ringing ..."));
						btnCancel.setEnabled(false);
						currentCallID = callId;
						btnCancel.setEnabled(true);
					}

					@Override
					public void onCallInvitationDeclined(String localUser, String localDomain, String reason) {
						textArea.setText(textArea.getText() + System.getProperty("line.separator") + " - "
								+ "Invitation Declined.");
						btnCancel.setEnabled(false);
						callButton.setEnabled(true);
					}
				});
			}
		});
	}
	
	private void setEndCallButton(JButton endCall) {
		endCall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sipuada.finishCall(currentCallID);
			}
		});
	}

	private void setUpRegisterButton(JButton registerButton) {

		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (sipuada != null) {
					sipuada.destroySipuada();
				}
				String username = registrarUserNameTextField.getText();
				sipuada = new Sipuada(SIPClientMain.this,
						username, registrarDomainTextField.getText(),
						passwordField.getText(),
//						"192.168.130.49:55002/TCP",
						"10.100.100.125:54899/TCP"); //150.165.11.157:65486
				sipuada.registerPlugin(new LibJitsiMediaSipuadaPlugin(username));
				sipuada.registerAddresses(new BasicRequestCallback() {

					@Override
					public void onRequestSuccess(String localUser, String localDomain, Object... response) {
						textArea.setText(textArea.getText()
								+ System.getProperty("line.separator") + " - "
								+ " successfully registered");
					}

					@Override
					public void onRequestFailed(String localUser, String localDomain, String reason) {
						textArea.setText(textArea.getText()
								+ System.getProperty("line.separator") + " - "
								+ " failure to register: " + reason);
					}

				}, 420);
			}
		});

	}

	private void setUpAcceptCallButton(JButton btAcceptCall) {
		btAcceptCall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sipuada.acceptCallInvitation(currentInviteCallID);
			}
		});
	}

	private void setUpRejectCallButton(JButton btRejectCall) {
		btRejectCall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sipuada.declineCallInvitation(currentInviteCallID);
			}
		});
	}

	private void setUpCancelButton(JButton btCancel) {
		btCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sipuada.cancelCallInvitation(currentCallID);
				btnCancel.setEnabled(false);
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSipuada = new JFrame();
		frmSipuada.setTitle("SIP User Agent - Sipuada");
		frmSipuada.setBounds(100, 100, 800, 600);
		frmSipuada.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblNewLabel = new JLabel("Domain");

		registrarDomainTextField = new JTextField();
		registrarDomainTextField.setColumns(10);

		JLabel label = new JLabel("User");

		registrarUserNameTextField = new JTextField();
		registrarUserNameTextField.setColumns(10);

		JLabel label_1 = new JLabel("Password");
		frmSipuada.getContentPane().setLayout(
				new MigLayout("", "[207px,grow][142px,grow][142px,grow][][]",
						"[15px][19px][][][][grow][][]"));
		frmSipuada.getContentPane().add(registrarUserNameTextField,
				"cell 1 1,growx,aligny top");
		frmSipuada.getContentPane().add(label_1,
				"cell 2 0,alignx left,aligny top");
		frmSipuada.getContentPane().add(registrarDomainTextField,
				"cell 0 1,growx,aligny top");
		frmSipuada.getContentPane().add(lblNewLabel,
				"cell 0 0,alignx left,aligny top");
		frmSipuada.getContentPane().add(label, "cell 1 0");

		passwordField = new JTextField();
		frmSipuada.getContentPane().add(passwordField,
				"cell 2 1,growx,aligny top");

		JButton btnRegister = new JButton("Register");
		setUpRegisterButton(btnRegister);
		frmSipuada.getContentPane().add(btnRegister, "cell 4 1");

		JLabel label_2 = new JLabel("Domain");
		frmSipuada.getContentPane().add(label_2, "cell 0 2");

		JLabel lblNewLabel_1 = new JLabel("User");
		frmSipuada.getContentPane().add(lblNewLabel_1, "cell 1 2");

		callerDomainTextField = new JTextField();
		frmSipuada.getContentPane()
				.add(callerDomainTextField, "cell 0 3,growx");
		callerDomainTextField.setColumns(10);

		callerUserTextField = new JTextField();
		frmSipuada.getContentPane().add(callerUserTextField, "cell 1 3,growx");
		callerUserTextField.setColumns(10);

		btCall = new JButton("Call");
		setUPCallButton(btCall);
		frmSipuada.getContentPane().add(btCall, "cell 4 3");
		
		JLabel lblLog = new JLabel("Log");
		frmSipuada.getContentPane().add(lblLog, "cell 0 4");

		textArea = new JTextArea();
		frmSipuada.getContentPane().add(textArea, "cell 0 5 5 1,grow");

		btAcceptCall = new JButton("Accept Call");
		btAcceptCall.setEnabled(false);
		setUpAcceptCallButton(btAcceptCall);
		frmSipuada.getContentPane().add(btAcceptCall, "cell 0 7");

		btRejectCall = new JButton("Reject Call");
		btRejectCall.setEnabled(false);
		setUpRejectCallButton(btRejectCall);
		frmSipuada.getContentPane().add(btRejectCall, "cell 1 7");

		btnEndCall = new JButton("End Call");
		frmSipuada.getContentPane().add(btnEndCall, "cell 2 7");
		btnEndCall.setEnabled(false);
		setEndCallButton(btnEndCall);

		btnCancel = new JButton("Cancel");
		setUpCancelButton(btnCancel);
		btnCancel.setEnabled(false);
		frmSipuada.getContentPane().add(btnCancel, "cell 4 7");
	}

	@Override
	public boolean onCallInvitationArrived(String localUser, String localDomain, String callId,
			String remoteUsername, String remoteHost, boolean shouldExpectEarlyMedia) {
		textArea.setText(textArea.getText()
				+ System.getProperty("line.separator") + " - "
				+ " Call Invitation from " + remoteUsername + "@" + remoteHost
				+ " Arrived." + (shouldExpectEarlyMedia ? "\n -  Expecting early media..." : ""));
		btAcceptCall.setEnabled(true);
		btRejectCall.setEnabled(true);
		this.currentInviteCallID = callId;
		return isBusy;
	}

	@Override
	public void onCallInvitationCanceled(String localUser, String localDomain, String reason, String callId) {
		textArea.setText(textArea.getText()
				+ System.getProperty("line.separator") + " - "
				+ " Call Invitation Canceled: " + reason);
		btAcceptCall.setEnabled(false);
		btRejectCall.setEnabled(false);
		btnCancel.setEnabled(false);
		btCall.setEnabled(true);
	}

	@Override
	public void onCallInvitationFailed(String localUser, String localDomain, String reason, String callId) {
		textArea.setText(textArea.getText()
				+ System.getProperty("line.separator") + " - "
				+ " Call Invitation Failed: " + reason);
		btAcceptCall.setEnabled(false);
		btRejectCall.setEnabled(false);
		btnCancel.setEnabled(false);
		btCall.setEnabled(true);

	}

	@Override
	public void onCallEstablished(String localUser, String localDomain, String callId) {
		textArea.setText(textArea.getText()
				+ System.getProperty("line.separator") + " - "
				+ " Call Established.");
		btAcceptCall.setEnabled(false);
		btRejectCall.setEnabled(false);
		btnCancel.setEnabled(false);
		btnEndCall.setEnabled(true);
		this.currentCallID = callId;
		isBusy = true;
	
		
	}

	@Override
	public void onCallFinished(String localUser, String localDomain, String callId) {
		textArea.setText(textArea.getText()
				+ System.getProperty("line.separator") + " - "
				+ " Call Finished.");
		btAcceptCall.setEnabled(false);
		btRejectCall.setEnabled(false);
		btnEndCall.setEnabled(false);
		btCall.setEnabled(true);
		isBusy = false;
	}

	@Override
	public void onCallFailure(String localUser, String localDomain, String reason, String callId) {
		textArea.setText(textArea.getText()
				+ System.getProperty("line.separator") + " - "
				+ " Call Failure: " + reason);
		btAcceptCall.setEnabled(false);
		btRejectCall.setEnabled(false);
		btnEndCall.setEnabled(false);
		btCall.setEnabled(true);
		isBusy = false;
	}

	@Override
	public void onMessageReceived(String localUser, String localDomain, String callId, String remoteUser,
			String remoteDomain, String content, String contentType, String... additionalHeaders) {
		
	}

}
