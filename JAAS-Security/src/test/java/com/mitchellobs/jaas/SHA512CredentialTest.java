package com.mitchellobs.jaas;

import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the SHA512 password checking
 */
public class SHA512CredentialTest {

	public static final String NIMDA_HASH =
			"+Dg4M4Vk5igddWvgi22gN3SC/ZA4MbzfdTFSPifTgcX1BeAmuD6xSEa5VvFWqq8z+gzWXRzT2eQrXM/wJ9O99w==";

	@Test
	public void testCheckAgainstStringOK() throws Exception {
		Credential credential = new SHA512Credential(NIMDA_HASH);
		Assert.assertTrue(credential.check("nimda"));
	}

	@Test
	public void testCheckAgainstStringNotOK() throws Exception {
		Credential credential = new SHA512Credential(NIMDA_HASH);
		Assert.assertFalse(credential.check("bad"));
	}

	@Test
	public void testCheckAgainstCredentialWrongType() throws Exception {
		Credential sha = new SHA512Credential(NIMDA_HASH);
		Credential password = new Password(NIMDA_HASH);
		Assert.assertFalse(sha.check(password));
	}


}
