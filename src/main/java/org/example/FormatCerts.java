package org.example;

import org.example.util.CertificateUtil;
import org.example.util.Excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class FormatCerts {
    public static void format() {
        //获取目录
        String rootPath;
        System.out.println(rootPath = System.getProperty("user.dir"));
        File file = new File(rootPath);
        String certPath = rootPath + "\\certs";
        List<Map> certList = new ArrayList<>();
        //筛选cer文件
        List<String> cerFile = getFileEndWith(certPath, ".cer");
        for (String s : cerFile) {
            try{
                X509Certificate certificate;
                certificate = CertificateUtil.getCertificate(s);
                if (certificate == null) throw new NullPointerException();
                else {
                    Map<String, String> dataMap=new HashMap<String, String>();
                    dataMap.put("Subject", CertificateUtil.getSubjectDN(certificate));
                    dataMap.put("Issuer", CertificateUtil.getIssuerDN(certificate));
                    dataMap.put("NotAfter", CertificateUtil.getNotAfter(certificate));
                    dataMap.put("PIN",CertificateUtil.getPin(certificate));
                    dataMap.put("IsRoot",CertificateUtil.isRoot(certificate));
                    certList.add(dataMap);
                }
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException at "+s);
            } catch (CertificateException e) {
                System.out.println("CertificateException at "+s);
            } catch (NullPointerException e){
                System.out.println("NullPointerException at "+s);
            }
        }
        Excel.writeExcel(certList,5,"D:/out.xlsx");
    }

    public static List<String> getFileEndWith(String directoryPath, String endRegex) {
        List<String> list = new ArrayList<String>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                if (file.getName().endsWith(endRegex)) {
                    list.add(file.getAbsolutePath());
                }
            }
        }
        return list;
    }
}
