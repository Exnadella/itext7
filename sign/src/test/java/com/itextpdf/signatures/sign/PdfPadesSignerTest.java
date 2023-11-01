/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfPadesSignerTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesSignerTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfPadesSignerTest/";
    private static final char[] password = "testpassphrase".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void directoryPathIsNotADirectoryTest()
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "directoryPathIsNotADirectoryTest.pdf";
        String outFileName = destinationFolder + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String caCertFileName = certsSrc + "rootRsa.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        IExternalSignature pks =
                new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);

        SignerProperties signerProperties = createSignerProperties();

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);

        padesSigner.setTemporaryDirectoryPath(destinationFolder + "newPdf.pdf");
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);

        Exception exception = Assert.assertThrows(PdfException.class,
                () -> padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, pks, testTsa));
        Assert.assertEquals(MessageFormatUtil.format(SignExceptionMessageConstant.PATH_IS_NOT_DIRECTORY,
                destinationFolder + "newPdf.pdf"), exception.getMessage());
    }
    
    @Test
    public void noSignaturesToProlongTest()
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "noSignaturesToProlongTest.pdf";
        String outFileName = destinationFolder + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String caCertFileName = certsSrc + "rootRsa.pem";

        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);

        Exception exception = Assert.assertThrows(PdfException.class,
                () -> padesSigner.prolongSignatures(testTsa));
        Assert.assertEquals(SignExceptionMessageConstant.NO_SIGNATURES_TO_PROLONG, exception.getMessage());
    }
    
    @Test
    public void defaultClientsCannotBeCreated()
            throws IOException, AbstractOperatorCreationException, AbstractPKCSException, CertificateException {
        String fileName = "defaultClientsCannotBeCreated.pdf";
        String outFileName = destinationFolder + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        IExternalSignature pks =
                new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);

        SignerProperties signerProperties = createSignerProperties();

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        padesSigner.setTemporaryDirectoryPath(destinationFolder);

        Exception exception = Assert.assertThrows(PdfException.class,
                () -> padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, pks, testTsa));
        Assert.assertEquals(SignExceptionMessageConstant.DEFAULT_CLIENTS_CANNOT_BE_CREATED, exception.getMessage());
    }

    @Test
    public void defaultSignerPropertiesTest()
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "defaultSignerPropertiesTest.pdf";
        String outFileName = destinationFolder + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String caCertFileName = certsSrc + "rootRsa.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        IExternalSignature pks =
                new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);

        SignerProperties signerProperties = new SignerProperties();

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);

        padesSigner.signWithBaselineLTAProfile(signerProperties, signRsaChain, pks, testTsa);

        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");
        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }
    
    private SignerProperties createSignerProperties() {
        SignerProperties signerProperties = new SignerProperties();
        signerProperties.setFieldName("Signature1");
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(signerProperties.getFieldName())
                .setContent("Approval test signature.\nCreated by iText.");
        signerProperties.setPageRect(new Rectangle(50, 650, 200, 100))
                .setSignatureAppearance(appearance);

        return signerProperties;
    }

    private PdfPadesSigner createPdfPadesSigner(String srcFileName, String outFileName) throws IOException {
        return new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)),
                FileUtil.getFileOutputStream(outFileName));
    }
}
