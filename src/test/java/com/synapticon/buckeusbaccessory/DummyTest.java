package com.synapticon.buckeusbaccessory;

import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

public class DummyTest {

    @Test
    public void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) 512);
        Assert.assertEquals(0, buffer.array()[1]);
        Assert.assertEquals(2, buffer.array()[0]);
    }
}
