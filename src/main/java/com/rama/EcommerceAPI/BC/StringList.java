package com.rama.EcommerceAPI.BC;

/**
 *
 * @author 20548
 */
public interface StringList
        extends Iterable<String> {

    /**
     *
     * @param s
     * @return
     */
    boolean add(String s);

    /**
     *
     * @param index
     * @return
     */
    String get(int index);

    /**
     *
     * @return
     */
    int size();

    /**
     *
     * @return
     */
    String[] toStringArray();

    /**
     * Return a section of the contents of the list. If the list is too short
     * the array is filled with nulls.
     *
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     * @return an array of length to - from
     */
    String[] toStringArray(int from, int to);
}
