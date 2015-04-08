package com.mitchellobs.jaas;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.util.security.Credential;

/**
 * Represents a SHA512 hashed password.
 */
public class SHA512Credential extends Credential {

	private String hash;

	public SHA512Credential(String hash) {
		this.hash = hash;
	}

	@Override
	public boolean check(Object o) {
		boolean ok = false;
		if (o instanceof Credential) {
			ok = equals(o);
		} else if (o instanceof String) {
			String s = (String) o;
			ok = hash.equals(Base64.encodeBase64String(DigestUtils.sha512(s)));
		}
		return ok;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SHA512Credential) {
			SHA512Credential sha512Credential = (SHA512Credential) obj;
			return hash.equals(sha512Credential.hash);
		}
		return false;
	}
}
