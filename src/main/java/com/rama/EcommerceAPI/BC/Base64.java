package com.rama.EcommerceAPI.BC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class for converting Base64 data to bytes and back again.
 */
public class Base64 {

    private static final Encoder encoder = new Base64Encoder();

    /**
     *
     * @param data
     * @return
     */
    public static String toBase64String(
            byte[] data) {
        return toBase64String(data, 0, data.length);
    }

    /**
     *
     * @param data
     * @param off
     * @param length
     * @return
     */
    public static String toBase64String(
            byte[] data,
            int off,
            int length) {
        byte[] encoded = encode(data, off, length);
        return Strings.fromByteArray(encoded);
    }

    /**
     * encode the input data producing a base 64 encoded byte array.
     *
     * @param data
     * @return a byte array containing the base 64 encoded data.
     */
    public static byte[] encode(
            byte[] data) {
        return encode(data, 0, data.length);
    }

    /**
     * encode the input data producing a base 64 encoded byte array.
     *
     * @param data
     * @param off
     * @param length
     * @return a byte array containing the base 64 encoded data.
     */
    public static byte[] encode(
            byte[] data,
            int off,
            int length) throws EncoderException {
        int len = (length + 2) / 3 * 4;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

        try {
            encoder.encode(data, off, length, bOut);
        } catch (Exception e) {
            throw new EncoderException("exception encoding base64 string: " + e.getMessage(), e);
        }

        return bOut.toByteArray();
    }

    /**
     * Encode the byte data to base 64 writing it to the given output stream.
     *
     * @param data
     * @param out
     * @return the number of bytes produced.
     * @throws IOException
     */
    public static int encode(
            byte[] data,
            OutputStream out)
            throws IOException {
        return encoder.encode(data, 0, data.length, out);
    }

    /**
     * Encode the byte data to base 64 writing it to the given output stream.
     *
     * @param data
     * @param off
     * @param length
     * @param out
     * @return the number of bytes produced.
     * @throws IOException
     */
    public static int encode(
            byte[] data,
            int off,
            int length,
            OutputStream out)
            throws IOException {
        return encoder.encode(data, off, length, out);
    }

    /**
     * decode the base 64 encoded input data. It is assumed the input data is
     * valid.
     *
     * @param data
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode(
            byte[] data) {
        int len = data.length / 4 * 3;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

        try {
            encoder.decode(data, 0, data.length, bOut);
        } catch (Exception e) {
            throw new DecoderException("unable to decode base64 data: " + e.getMessage(), e);
        }

        return bOut.toByteArray();
    }

    /**
     * decode the base 64 encoded String data - whitespace will be ignored.
     *
     * @param data
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode(
            String data) {
        int len = data.length() / 4 * 3;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

        try {
            encoder.decode(data, bOut);
        } catch (Exception e) {
            throw new DecoderException("unable to decode base64 string: " + e.getMessage(), e);
        }

        return bOut.toByteArray();
    }

    /**
     * decode the base 64 encoded String data writing it to the given output
     * stream, whitespace characters will be ignored.
     *
     * @param data
     * @param out
     * @return the number of bytes produced.
     * @throws IOException
     */
    public static int decode(
            String data,
            OutputStream out)
            throws IOException {
        return encoder.decode(data, out);
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static byte[] encodeBase64(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
