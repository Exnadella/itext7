/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.bouncycastlefips.asn1.tsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.tsp.MessageImprint;

/**
 * Wrapper class for {@link MessageImprint}.
 */
public class MessageImprintBCFips extends ASN1EncodableBCFips implements IMessageImprint {
    /**
     * Creates new wrapper instance for {@link MessageImprint}.
     *
     * @param messageImprint {@link MessageImprint} to be wrapped
     */
    public MessageImprintBCFips(MessageImprint messageImprint) {
        super(messageImprint);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link MessageImprint}.
     */
    public MessageImprint getMessageImprint() {
        return (MessageImprint) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getHashedMessage() {
        return getMessageImprint().getHashedMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBCFips(getMessageImprint().getHashAlgorithm());
    }
}
