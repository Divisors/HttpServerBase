package com.divisors.projectcuttlefish.crypto.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.swing.JOptionPane;

import org.shredzone.acme4j.AcmeClient;
import org.shredzone.acme4j.AcmeClientFactory;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.challenge.TlsSni01Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.exception.AcmeUnauthorizedException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;

public class AcmePrivateKey {
	public static final int KEY_SIZE = 2048;

	protected final File USER_KEY_FILE = new File("user1.key");
	protected final File DOMAIN_KEY_FILE = new File("domain1.key");
	protected final File DOMAIN_CERT_FILE = new File("domain1.crt");
	protected final File DOMAIN_CSR_FILE = new File("domain1.crt");
	protected final List<String> domains;

	public AcmePrivateKey(List<String> domains) {
		this.domains = domains;
	}

	public void update() throws AcmeException, IOException {
		// Load or create a key pair for the user's account
		boolean createdNewKeyPair = false;

		KeyPair userKeyPair;
		if (USER_KEY_FILE.exists()) {
			try (FileReader fr = new FileReader(USER_KEY_FILE)) {
				userKeyPair = KeyPairUtils.readKeyPair(fr);
			}
		} else {
			userKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
			try (FileWriter fw = new FileWriter(USER_KEY_FILE)) {
				KeyPairUtils.writeKeyPair(userKeyPair, fw);
			}
			createdNewKeyPair = true;
		}

		// Create an AcmeClient for Let's Encrypt
		// Use "acme://letsencrypt.org" for production server
		AcmeClient client = AcmeClientFactory.connect("acme://letsencrypt.org/staging");

		// Register a new user
		Registration reg = new Registration(userKeyPair);
		try {
			client.newRegistration(reg);
			System.out.println("Registered a new user, URI: " + reg.getLocation());
		} catch (AcmeConflictException ex) {
			System.out.println("Account does already exist, URI: " + reg.getLocation());
		}

		System.out.println("Terms of Service: " + reg.getAgreement());

		if (createdNewKeyPair) {
			boolean accepted = acceptAgreement(client, reg);
			if (!accepted) {
				return;
			}
		}

		for (String domain : domains) {
			// Create a new authorization
			Authorization auth = new Authorization();
			auth.setDomain(domain);
			try {
				client.newAuthorization(reg, auth);
			} catch (AcmeUnauthorizedException ex) {
				// Maybe there are new T&C to accept?
				boolean accepted = acceptAgreement(client, reg);
				if (!accepted) {
					return;
				}
				// Then try again...
				client.newAuthorization(reg, auth);
			}
			System.out.println("New authorization for domain " + domain);

			// Uncomment a challenge...
			Challenge challenge = httpChallenge(auth, reg, domain);
			// Challenge challenge = dnsChallenge(auth, reg, domain);
			// Challenge challenge = tlsSniChallenge(auth, reg, domain);

			if (challenge == null) {
				return;
			}

			// Trigger the challenge
			client.triggerChallenge(reg, challenge);

			// Poll for the challenge to complete
			int attempts = 10;
			while (challenge.getStatus() != Status.VALID && attempts-- > 0) {
				if (challenge.getStatus() == Status.INVALID) {
					System.err.println("Challenge failed... Giving up.");
					return;
				}
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException ex) {
					System.err.println("[WARN]interrupted");
					ex.printStackTrace();
				}
				client.updateChallenge(challenge);
			}
			if (attempts == 0) {
				System.err.println("Failed to pass the challenge... Giving up.");
				return;
			}
		}

		// Load or create a key pair for the domain
		KeyPair domainKeyPair;
		if (DOMAIN_KEY_FILE.exists()) {
			try (FileReader fr = new FileReader(DOMAIN_KEY_FILE)) {
				domainKeyPair = KeyPairUtils.readKeyPair(fr);
			}
		} else {
			domainKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
			try (FileWriter fw = new FileWriter(DOMAIN_KEY_FILE)) {
				KeyPairUtils.writeKeyPair(domainKeyPair, fw);
			}
		}

		// Generate a CSR for the domain
		CSRBuilder csrb = new CSRBuilder();
		csrb.addDomains(domains);
		csrb.sign(domainKeyPair);

		try (Writer out = new FileWriter(DOMAIN_CSR_FILE)) {
			csrb.write(out);
		}

		// Request a signed certificate
		URI certificateUri = client.requestCertificate(reg, csrb.getEncoded());
		System.out.println("Success! The certificate for domains " + domains + " has been generated!");
		System.out.println("Certificate URI: " + certificateUri);

		// Download the certificate
		X509Certificate cert = client.downloadCertificate(certificateUri);
		try (FileWriter fw = new FileWriter(DOMAIN_CERT_FILE)) {
			CertificateUtils.writeX509Certificate(cert, fw);
		}

		// Revoke the certificate (uncomment if needed...)
		// client.revokeCertificate(reg, cert);
	}
	/**
     * Prepares HTTP challenge.
     */
    public Challenge httpChallenge(Authorization auth, Registration reg, String domain) throws AcmeException {
        // Find a single http-01 challenge
        Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
        if (challenge == null) {
        	System.err.println("Found no " + Http01Challenge.TYPE + " challenge, don't know what to do...");
            return null;
        }

        // Authorize the challenge
        challenge.authorize(reg);

        // Output the challenge, wait for acknowledge...
        System.out.println("Please create a file in your web server's base directory.");
        System.out.println("It must be reachable at: http://" + domain + "/.well-known/acme-challenge/" + challenge.getToken());
        System.out.println("File name: " + challenge.getToken());
        System.out.println("Content: " + challenge.getAuthorization());
        System.out.println("The file must not contain any leading or trailing whitespaces or line breaks!");
        System.out.println("If you're ready, dismiss the dialog...");

        StringBuilder message = new StringBuilder();
        message.append("Please create a file in your web server's base directory.\n\n");
        message.append("http://").append(domain).append("/.well-known/acme-challenge/").append(challenge.getToken()).append("\n\n");
        message.append("Content:\n\n");
        message.append(challenge.getAuthorization());
        int option = JOptionPane.showConfirmDialog(null,
                        message.toString(),
                        "Prepare Challenge",
                        JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) {
        	System.err.println("User cancelled challenge");
            return null;
        }

        return challenge;
    }

    /**
     * Prepares DNS challenge.
     */
    public Challenge dnsChallenge(Authorization auth, Registration reg, String domain) throws AcmeException {
        // Find a single dns-01 challenge
        Dns01Challenge challenge = auth.findChallenge(Dns01Challenge.TYPE);
        if (challenge == null) {
        	System.err.println("Found no " + Dns01Challenge.TYPE + " challenge, don't know what to do...");
            return null;
        }

        // Authorize the challenge
        challenge.authorize(reg);

        // Output the challenge, wait for acknowledge...
        System.out.println("Please create a TXT record:");
        System.out.println("_acme-challenge." + domain + ". IN TXT " + challenge.getDigest());
        System.out.println("If you're ready, dismiss the dialog...");

        StringBuilder message = new StringBuilder();
        message.append("Please create a TXT record:\n\n");
        message.append("_acme-challenge." + domain + ". IN TXT " + challenge.getDigest());
        int option = JOptionPane.showConfirmDialog(null,
                        message.toString(),
                        "Prepare Challenge",
                        JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) {
            System.err.println("User cancelled challenge");
            return null;
        }

        return challenge;
    }

    /**
     * Prepares TLS-SNI challenge.
     */
    public Challenge tlsSniChallenge(Authorization auth, Registration reg, String domain) throws AcmeException {
        // Find a single tls-sni-01 challenge
        TlsSni01Challenge challenge = auth.findChallenge(TlsSni01Challenge.TYPE);
        if (challenge == null) {
        	System.err.println("Found no " + TlsSni01Challenge.TYPE + " challenge, don't know what to do...");
            return null;
        }

        // Authorize the challenge
        challenge.authorize(reg);

        // Get the Subject
        String subject = challenge.getSubject();

        // Create a keypair
        KeyPair domainKeyPair;
        try (FileWriter fw = new FileWriter("tlssni.key")) {
            domainKeyPair = KeyPairUtils.createKeyPair(2048);
            KeyPairUtils.writeKeyPair(domainKeyPair, fw);
        } catch (IOException ex) {
            System.err.println("Could not create keypair");
            ex.printStackTrace();
            return null;
        }

        // Create a certificate
        try (FileWriter fw = new FileWriter("tlssni.crt")) {
            X509Certificate cert = CertificateUtils.createTlsSniCertificate(domainKeyPair, subject);
            CertificateUtils.writeX509Certificate(cert, fw);
        } catch (IOException ex) {
            System.err.println("Could not create certificate");
            ex.printStackTrace();
            return null;
        }

        // Output the challenge, wait for acknowledge...
        System.out.println("Please configure your web server.");
        System.out.println("It must return the certificate 'tlssni.crt' on a SNI request to:");
        System.out.println(subject);
        System.out.println("The matching keypair is available at 'tlssni.key'.");
        System.out.println("If you're ready, dismiss the dialog...");

        StringBuilder message = new StringBuilder();
        message.append("Please use 'tlssni.key' and 'tlssni.crt' cert for SNI requests to:\n\n");
        message.append("https://").append(subject).append("\n\n");
        int option = JOptionPane.showConfirmDialog(null,
                        message.toString(),
                        "Prepare Challenge",
                        JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) {
            System.err.println("User cancelled challenge");
            return null;
        }

        return challenge;
    }

	/**
	 * Presents the user a link to the Terms of Service, and asks for confirmation.
     *
     * @param client
     *            {@link AcmeClient} to send confirmation to
     * @param reg
     *            {@link Registration} User's registration, containing the Agreement URI
     * @return {@code true}: User confirmed, {@code false} user rejected
     */
    public boolean acceptAgreement(AcmeClient client, Registration reg)
                throws AcmeException {
        int option = JOptionPane.showConfirmDialog(null,
                        "Do you accept the Terms of Service?\n\n" + reg.getAgreement(),
                        "Accept T&C",
                        JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.NO_OPTION) {
            System.err.println("User did not accept Terms of Service");
            return false;
        }

        client.modifyRegistration(reg);
        System.out.println("Updated user's ToS");

        return true;
    }
}
