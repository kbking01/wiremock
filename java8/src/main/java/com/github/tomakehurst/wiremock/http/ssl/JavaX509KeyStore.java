package com.github.tomakehurst.wiremock.http.ssl;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static java.util.Objects.requireNonNull;

/**
 * Wrapper class to make it easy to retrieve X509 PrivateKey and certificate
 * chains
 */
public class JavaX509KeyStore implements X509KeyStore {

    private final KeyStore keyStore;
    private final char[] password;
    private final List<String> aliases;

    /**
     *
     * @param keyStore {@link KeyStore} to delegate to
     * @param password used to manage all keys stored in this key store
     * @throws KeyStoreException if the keystore has not been loaded
     */
    public JavaX509KeyStore(KeyStore keyStore, char[] password) throws KeyStoreException {
        this.keyStore = requireNonNull(keyStore);
        this.password = requireNonNull(password);
        this.aliases = Collections.list(keyStore.aliases());
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        try {
            Key key = keyStore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                return (PrivateKey) key;
            } else {
                return null;
            }
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException e) {
            return null;
        } catch (KeyStoreException e) {
            // impossible, class could not have been constructed
            return throwUnchecked(e, null);
        }
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        try {
            Certificate[] fromKeyStore = keyStore.getCertificateChain(alias);
            if (fromKeyStore != null && areX509Certificates(fromKeyStore)) {
                return convertToX509(fromKeyStore);
            } else {
                return null;
            }
        } catch (KeyStoreException e) {
            return throwUnchecked(e, null);
        }
    }

    private static boolean areX509Certificates(Certificate[] fromKeyStore) {
        return fromKeyStore.length == 0 || fromKeyStore[0] instanceof X509Certificate;
    }

    private static X509Certificate[] convertToX509(Certificate[] fromKeyStore) {
        X509Certificate[] result = new X509Certificate[fromKeyStore.length];
        for (int i = 0; i < fromKeyStore.length; i++) {
            result[i] = (X509Certificate) fromKeyStore[i];
        }
        return result;
    }

    @Override
    public boolean hasCertificateAuthority() {
        return getCertificateAuthority() != null;
    }

    @Override
    public CertificateAuthority getCertificateAuthority() {
        for (String alias : aliases) {
            X509Certificate[] chain = getCertificateChain(alias);
            PrivateKey key = getPrivateKey(alias);
            if (isCertificateAuthority(chain[0]) && key != null) {
                return new CertificateAuthority(chain, key);
            }
        }
        return null;
    }

    private static boolean isCertificateAuthority(X509Certificate certificate) {
        boolean[] keyUsage = certificate.getKeyUsage();
        return keyUsage != null && keyUsage.length > 5 && keyUsage[5];
    }

    @Override
    public void setKeyEntry(String alias, CertChainAndKey newCertChainAndKey) throws KeyStoreException {
        keyStore.setKeyEntry(alias, newCertChainAndKey.key, password, newCertChainAndKey.certificateChain);
    }
}
