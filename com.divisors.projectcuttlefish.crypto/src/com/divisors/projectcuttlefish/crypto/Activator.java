
package com.divisors.projectcuttlefish.crypto;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.shredzone.acme4j.AcmeClient;
import org.shredzone.acme4j.AcmeClientFactory;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeUnauthorizedException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;

public class Activator implements BundleActivator {
	private static Activator instance;
	protected BundleContext context;
	protected Path stateLoc;

	public static Activator getInstance() {
		return instance;
	}

	public BundleContext getContext() {
		return context;
	}
	public Path getStateLocation() {
		return stateLoc;
	}
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		this.context = context;
		IPath path = Platform.getStateLocation(context.getBundle());
		if (path != null)
			stateLoc = path.makeAbsolute().toFile().toPath();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.instance = null;
	}
	public void runAcme() {
		File userKeyFile = new File(getStateLocation().toFile(), "user.key");
		File DOMAIN_KEY_FILE = new File(getStateLocation().toFile(), "domain.key");
		File DOMAIN_CERT_FILE = new File(getStateLocation().toFile(), "domain.key");
		String domain = "projcuttlefish.ddns.net";
		final int KEY_SIZE = 2048;
		boolean createdNewKeyPair = false;
		
		KeyPair userKeyPair;
		if (userKeyFile.exists()) {
            try (FileReader fr = new FileReader(userKeyFile)) {
                userKeyPair = KeyPairUtils.readKeyPair(fr);
            }
        } else {
            userKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
            try (FileWriter fw = new FileWriter(userKeyFile)) {
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
//            Challenge challenge = dnsChallenge(auth, reg, domain);
//            Challenge challenge = tlsSniChallenge(auth, reg, domain);

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
            	ex.printStackTrace();
            }
            client.updateChallenge(challenge);
        }
        if (attempts == 0) {
        	System.err.println("Failed to pass the challenge... Giving up.");
            return;
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
        System.out.println("Success! The certificate for domains " + domain + " has been generated!");
        System.out.println("Certificate URI: " + certificateUri);
        
        // Download the certificate
        X509Certificate cert = client.downloadCertificate(certificateUri);
        try (FileWriter fw = new FileWriter(DOMAIN_CERT_FILE)) {
            CertificateUtils.writeX509Certificate(cert, fw);
        }

        // Revoke the certificate (uncomment if needed...)
        // client.revokeCertificate(reg, cert);
	}

	private Challenge httpChallenge(Authorization auth, Registration reg, String domain) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean acceptAgreement(AcmeClient client, Registration reg) {
		// TODO Auto-generated method stub
		return false;
	}
}
