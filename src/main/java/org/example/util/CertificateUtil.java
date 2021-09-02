package org.example.util;

import okhttp3.CertificatePinner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtil {

    //get x509 cert
    public static X509Certificate getCertificate(String path) throws FileNotFoundException, CertificateException {
        FileInputStream inputStream;
        X509Certificate x509Certificate;
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        inputStream = new FileInputStream(path);
        x509Certificate = (X509Certificate) factory.generateCertificate(inputStream);
        return x509Certificate;
    }

    public static String getSubjectDN(X509Certificate certificate) {
        String s = certificate.getSubjectDN().toString();
        s = s.substring("CN=".length(), s.indexOf(','));
        return s;
    }

    public static String getIssuerDN(X509Certificate certificate) {
        String s = certificate.getIssuerDN().toString();
        s = s.substring("CN=".length(), s.indexOf(','));
        return s;
    }

    public static String getNotAfter(X509Certificate certificate) {
        return certificate.getNotAfter().toString();
    }

    public static String getPin(X509Certificate certificate) {
        String pin = CertificatePinner.pin(certificate);
        pin = pin.substring("sha256/".length());
        return pin;
    }

    public static String isRoot(X509Certificate certificate) {
        return String.valueOf(certificate.getSubjectDN().equals(certificate.getIssuerDN()));
    }
}
