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
package com.itextpdf.bouncycastlefips.openssl;

import com.itextpdf.commons.bouncycastle.openssl.AbstractPEMException;

import java.util.Objects;
import org.bouncycastle.openssl.PEMException;

/**
 * Wrapper class for {@link PEMException}.
 */
public class PEMExceptionBCFips extends AbstractPEMException {
    private final PEMException pemException;

    /**
     * Creates new wrapper instance for {@link PEMException}.
     *
     * @param pemException {@link PEMException} to be wrapped
     */
    public PEMExceptionBCFips(PEMException pemException) {
        this.pemException = pemException;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link PEMException}.
     */
    public PEMException getPemException() {
        return pemException;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PEMExceptionBCFips that = (PEMExceptionBCFips) o;
        return Objects.equals(pemException, that.pemException);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(pemException);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return pemException.toString();
    }

    /**
     * Delegates {@code getMessage} method call to the wrapped exception.
     */
    @Override
    public String getMessage() {
        return pemException.getMessage();
    }
}
