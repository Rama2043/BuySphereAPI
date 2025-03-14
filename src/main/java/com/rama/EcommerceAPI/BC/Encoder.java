package com.rama.EcommerceAPI.BC;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Encode and decode byte arrays (typically from binary to 7-bit ASCII
 * encodings).
 */
public interface Encoder {

    /**
     *
     * @param data
     * @param off
     * @param length
     * @param out
     * @return
     * @throws IOException
     */
    int encode(byte[] data, int off, int length, OutputStream out) throws IOException;

    /**
     *
     * @param data
     * @param off
     * @param length
     * @param out
     * @return
     * @throws IOException
     */
    int decode(byte[] data, int off, int length, OutputStream out) throws IOException;

    /**
     *
     * @param data
     * @param out
     * @return
     * @throws IOException
     */
    int decode(String data, OutputStream out) throws IOException;
}
