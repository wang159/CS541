/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bufmgr;

/**
 *
 * @author wei
 */
public class BufferPoolExceededException extends Exception {

    public BufferPoolExceededException(Object object, String buffer_is_full) {
    }
    
}