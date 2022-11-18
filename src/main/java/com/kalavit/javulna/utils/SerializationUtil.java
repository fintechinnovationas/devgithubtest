/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalavit.javulna.utils;

import java.io.*;

import com.kalavit.javulna.model.User;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peti
 */
public class SerializationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SerializationUtil.class);

    public static byte[] serialize(Object o) {

        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(o);
            out.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException ex) {
                LOG.error("Error during serialize", ex);
            }
        }
    }

    public static User readUserFromFile(byte[] byteArray) {
        return SerializationUtils.deserialize(byteArray);
    }

}
