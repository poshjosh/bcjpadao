package com.bc.jpa.paging;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)BatchUtils.java   26-Jul-2014 20:19:00
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class PagingUtil {

    private transient static final Logger LOG = Logger.getLogger(PagingUtil.class.getName());
    
    public static int getBatch(int index, int batchSize) {
        if(index < 0) {
            throw new IllegalArgumentException("index < 0");
        }
        if(batchSize <= 0) {
            throw new IllegalArgumentException("batchSize <= 0");
        }
        int output = index / batchSize;
        return output;
    }
    
    public static int getIndexInBatch(int index, int batchSize) {
        if(index < 0) {
            throw new IllegalArgumentException("index < 0");
        }
        if(batchSize <= 0) {
            throw new IllegalArgumentException("batchSize <= 0");
        }
        int output = index % batchSize;
        return output;
    }

    public static int getBatchCount(int batchSize, int size) {
        if(batchSize <= 0) {
            throw new IllegalArgumentException("batchSize <= 0");
        }
        if (size <= 0) {
            return 0;
        }        
        int batchCount = size / batchSize;
        if (size % batchSize > 0) {
            ++batchCount;
        }        
        return batchCount;
    }
    
    public static int getStart(int batch, int batchSize, int size) {
        return getStart(batch, batchSize, size, true, false);
    }
    
    public static int getStart(int batch, int batchSize, int size, 
            boolean forward, boolean firstElementZero) {
        if(forward) {
            return getForwardStart(batch, batchSize, size, firstElementZero);
        }else{
            return getReverseStart(batch, batchSize, size, firstElementZero);
        }
    }

    public static int getEnd(int batch, int batchSize, int size) {
        return getEnd(batch, batchSize, size, true, false);
    }
    
    public static int getEnd(int batch, int batchSize, int size, 
            boolean forward, boolean firstElementZero) {
        if (forward) {
            return getForwardEnd(batch, batchSize, size, firstElementZero);
        }else{
            return getReverseEnd(batch, batchSize, size, firstElementZero);
        }    
    }

    private static int getForwardStart(int batch, int batchSize, 
            int size, boolean firstElementZero) {
        
        final int batchCount = getBatchCount(batchSize, size);
        
        logFiner("Batch: {0}, batchSize: {1}, size: {2}", batch, batchSize, size);

        if (size <= 0) {
            return 0;
        }    
        if (batch < 0) {
            throw new IndexOutOfBoundsException("Batch: "+batch+" is less than 0");
        }    
        if (batch >= batchCount) {
            throw new IndexOutOfBoundsException("Batch: "+batch+" is greater than batchCount: "+batchCount);
        }        
        int batchStart = (batch * batchSize) + (firstElementZero?0:1);
        
        logFiner("Forward. batch start: {0}.", batchStart);

        return batchStart;
    }

    private static int getReverseStart(int batch, int batchSize, 
            int size, boolean firstElementZero) {
        
        logFiner("Batch: {0}, batchSize: {1}, size: {2}.", batch, batchSize, size);
        
        int batchStart = getForwardStart(batch, batchSize, size, firstElementZero);
        int reverseStart = (size - batchStart) + (firstElementZero?0:1);
        
        logFiner("Reverse. batch start: {0}.", reverseStart);
    
        return reverseStart;
    }

    private static int getForwardEnd(int batch, int batchSize, 
            int size, boolean firstElementZero) {
        
        int start = getStart(batch, batchSize, size, true, firstElementZero) - (firstElementZero?0:1);
        
        logFiner("Start: {0}, batchSize: {1}, size: {2}.", start, batchSize, size);
        
        if (size <= 0) {
            return 0;
        } else {
            int forwardEnd = start + batchSize;
            forwardEnd = forwardEnd <= size ? forwardEnd : size;
            
            logFiner("Forward. batch end: {0}.", forwardEnd);
            
            return forwardEnd;
        }
    }

    private static int getReverseEnd(int batch, int batchSize, 
            int size, boolean firstElementZero) {
        
        int start = getStart(batch, batchSize, size, false, firstElementZero) + (firstElementZero?0:1);
        
        logFiner("Start: {0}, batchSize: {1}, size: {2}.", start, batchSize, size);

        if (size <= 0) {
            return 0;
        } else {
            int reverseEnd = start - batchSize;
            reverseEnd = reverseEnd >= 0 ? reverseEnd : 0;
            
            logFiner("Reverse. batch end: {0}.", reverseEnd);

            return reverseEnd;
        }
    }
    
    private static void logFiner(String fmt, Object arg) {
        LOG.log(Level.FINER, fmt, arg);
    }

    private static void logFiner(String fmt, Object arg0, Object arg1, Object arg2) {
        LOG.finer(() -> MessageFormat.format(fmt, arg0, arg1, arg2));
    }
}
